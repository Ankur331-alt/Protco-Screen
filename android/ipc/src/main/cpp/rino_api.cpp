#include <jni.h>
#include <sys/socket.h>
#include <string.h>

#define RinoP2PHeadTag 0x48595A58

//小端字节序：低位字节在前，高位字节在后
//#pragma pack(1)
typedef struct {
    unsigned int    u32HeadTag;                 //消息头
    unsigned short  u16CmdId;                   //命令字
    unsigned int    u32MsgBodyLen;              //命令参数长度
    unsigned char   u8Version;                  //通信协议版本
    unsigned char   u8segment;                  //视频buf是否分段
    unsigned char   u8SignCode;                 //标识码
    unsigned char   u8IsResponse;               //是否是应答
    unsigned char   u8DevType;                  //设备类型
} rino_p2p_head_t;
//#pragma pack ()

typedef struct {
    rino_p2p_head_t stMsgHead;
    /*
    unsigned char *pJsonMsgbody;
     */
} rino_p2p_packet_t;

typedef enum {

    P2P_VIDEO_PFRAME = 0,									/**< 0 */
    P2P_VIDEO_IFRAME = 1,
    P2P_AUDIO_FRAME = 2,
} rino_p2p_frametype_e;

typedef enum {

    P2P_VIDEO_H264 = 0,										/**< 0 */
    P2P_VIDEO_H265 = 1,
    P2P_VIDEO_YUV = 2,
    P2P_VIDEO_JPEG = 3,

    P2P_AUDIO_PCM = 4,
    P2P_AUDIO_AAC = 5,
} rino_p2p_meidaformat_e;

//#pragma pack(1)
typedef struct {

    unsigned int        		u32Width;      	//分辨率宽度  4字节
    unsigned int        		u32Height;      //分辨率高度  4字节
    rino_p2p_meidaformat_e     	eFormat;  		//编码格式    4字节
} rino_p2p_videoinfo_t;
//#pragma pack ()

//#pragma pack(1)
typedef struct {

    unsigned int 				u32Samplerate;  //采样频率  4字节
    rino_p2p_meidaformat_e     	eFormat;  		//编码格式  4字节
} rino_p2p_audioinfo_t;
//#pragma pack ()

//#pragma pack(1)
typedef struct {
    unsigned char *  			pDataBuf;		//数据  8字节
    unsigned int     			u32BufSize;     //数据大小 4字节
    rino_p2p_frametype_e     	eType;  		//帧类型  4字节
    unsigned int        		u32Seq;   		//帧序号  4字节
    unsigned long long  		u64Timestamp;  	//时间戳  8字节

    union
    {
        rino_p2p_videoinfo_t 	stVideoInfo;  //12字节
        rino_p2p_audioinfo_t 	stAudioInfo;  //8字节
    } media_packet_info;
} rino_p2p_mediapacket_t;
//#pragma pack ()

extern int byteArrayToIntBigEndian(jbyte bytes[]);  //大端模式
extern  int byteArrayToIntLittleEndian(jbyte bytes[]); //小端模式



extern "C" {



JNIEXPORT jint JNICALL
Java_com_rino_p2pRino_Util_RINO_1APIs_getSocketType (
        JNIEnv *env, jobject obj, jint socket_fd)
{
    int type;
    socklen_t t = sizeof(int);
    if (getsockopt(socket_fd, SOL_SOCKET, SO_TYPE, &type, &t) < 0)
    {
        return -1;
    }
    if (type == SOCK_STREAM) return 1;
    else return 0;
}

JNIEXPORT jbyteArray JNICALL
Java_com_rino_p2pRino_Util_RINO_1APIs_getStructCmdByteToArray (
        JNIEnv *env, jobject obj, jstring jsonStr)
{

    int u32MsgBodyLen = env->GetStringLength(jsonStr);

    rino_p2p_head_t sendCmd;
    sendCmd.u32HeadTag = RinoP2PHeadTag;
    sendCmd.u16CmdId = 0x02;
    sendCmd.u32MsgBodyLen = u32MsgBodyLen;
    sendCmd.u8Version = 0x0A;
    sendCmd.u8segment = 0x00;
    sendCmd.u8SignCode = 0x0B;
    sendCmd.u8IsResponse = 0x01;
    sendCmd.u8DevType = 0x0C;

    unsigned char jsonStrArr[1024 * 2]  = { 0 };
    if (u32MsgBodyLen > 0)
    {
        env->GetStringUTFRegion(jsonStr, 0, u32MsgBodyLen, (char *)jsonStrArr);
    }

    rino_p2p_packet_t sendCmdPacket;
    sendCmdPacket.stMsgHead = sendCmd;
    /*
    sendCmdPacket.pJsonMsgbody = jsonStrArr;
    */
    int len = sizeof(sendCmdPacket);
    jbyteArray arraySend = env->NewByteArray(len);
    env->SetByteArrayRegion(arraySend, 0, len, (jbyte *)&sendCmdPacket);

    return arraySend;

}

JNIEXPORT jbyteArray JNICALL
Java_com_rino_p2pRino_Util_RINO_1APIs_sendAudioDataByteToArray (
        JNIEnv *env, jobject obj, jint frameSize, jlong seq, jlong u64Timestamp, jbyteArray pDataBuf)
{

    rino_p2p_mediapacket_t stMediaPacket = {0};

    stMediaPacket.eType = P2P_AUDIO_FRAME;
    stMediaPacket.media_packet_info.stAudioInfo.u32Samplerate = 16000;
    stMediaPacket.media_packet_info.stAudioInfo.eFormat = P2P_AUDIO_AAC;
    stMediaPacket.u32BufSize = frameSize;
    stMediaPacket.u32Seq = seq;
    stMediaPacket.u64Timestamp = u64Timestamp;

    // jbyteArray转为jbyte*
    jbyte* buf_byte = env->GetByteArrayElements(pDataBuf, JNI_FALSE);
    if (buf_byte == NULL)
    {
        return NULL;
    }
    // 拷贝数据
    unsigned char* buf_new = new unsigned char[frameSize];
    if (buf_new == NULL)
    {
        return NULL;
    }
    memcpy(buf_new, buf_byte, frameSize);
    stMediaPacket.pDataBuf = buf_new;

    int len = sizeof(stMediaPacket);
    // int len = 40;
    jbyteArray arraySend = env->NewByteArray(len);
    env->SetByteArrayRegion(arraySend, 0, len, (jbyte *)&stMediaPacket);

    return arraySend;

}


JNIEXPORT jbyteArray JNICALL
Java_com_rino_p2pRino_Util_RINO_1APIs_getCmdJsonByteToArray (
        JNIEnv *env, jobject obj, jstring jsonStr){

    int len = env->GetStringLength(jsonStr);
    unsigned char jsonStrArr[1024 * 2]  = { 0 };
    if (len > 0)
    {
        env->GetStringUTFRegion(jsonStr, 0, len, (char *)jsonStrArr);
    }

    jbyteArray arraySend = env->NewByteArray(len);
    env->SetByteArrayRegion(arraySend, 0, len, (jbyte *)&jsonStrArr);

    return arraySend;
}

JNIEXPORT jboolean JNICALL
Java_com_rino_p2pRino_Util_RINO_1APIs_checkCmdByteToBool (
        JNIEnv *env, jobject obj, jbyteArray checkStr){

    int len = env->GetArrayLength(checkStr);
    jbyte jsonbuf[4]  = { 0 };
    if (len > 0)
    {
        env->GetByteArrayRegion(checkStr, 0, len, jsonbuf);
    }


    if(byteArrayToIntBigEndian(jsonbuf) ==  RinoP2PHeadTag){
        return true;
    }else{
        return false;
    }

}

JNIEXPORT jint JNICALL
Java_com_rino_p2pRino_Util_RINO_1APIs_getMediaPacketSize (
        JNIEnv *env, jobject obj){

    // int32_t s32PacketLen = sizeof(rino_p2p_mediapacket_t); //虚拟机
    int32_t s32PacketLen = 44;  //真机
    return s32PacketLen;
}

JNIEXPORT jint JNICALL
Java_com_rino_p2pRino_Util_P2PUtil_setMediaPacketHead (
        JNIEnv *env, jobject obj, jbyteArray packetHead){

    int len = env->GetArrayLength(packetHead);
    rino_p2p_mediapacket_t pstMediaPacket = {0};
    jbyte packet[]  = { 0 };
    if (len > 0)
    {
        env->GetByteArrayRegion(packetHead, 0, len, packet);
    }



    return 0;
}

}
/**
* 字节数组转int 大端模式
*/
int byteArrayToIntBigEndian(jbyte *bytes) {
    int x = 0;
    for (int i = 0; i < 4; i++) {
        x <<= 8;
        int b = bytes[i] & 0xFF;
        x |= b;
    }
    return x;
}


/**
  * 字节数组转int 小端模式
  */
int byteArrayToIntLittleEndian(jbyte bytes[]){
    int x = 0;
    for (int i = 0; i < 4; i++) {
        int b = (bytes[i] & 0xFF) << (i * 8);
        x |= b;
    }
    return x;
}


