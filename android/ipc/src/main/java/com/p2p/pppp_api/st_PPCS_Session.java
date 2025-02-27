package com.p2p.pppp_api;

public class st_PPCS_Session {
	int Skt=-1;
	byte[] 	RemoteIP	=new byte[16];
	int		RemotePort	=0;
	
	byte[] 	MyLocalIP	=new byte[16];
	int		MyLocalPort	=0;
	
	byte[]  MyWanIP		=new byte[16];
	int		MyWanPort	=0;
	
	int		ConnectTime	=0;
	byte[]	DID=new byte[24];
	byte	bCorD=0;
	byte	bMode=0;
	
	public static String bytes2Str(byte[] bytes) {
    	String str;
		int iLen;
		for(iLen=0; iLen<bytes.length; iLen++){
			if(bytes[iLen]==(byte)0) break;
		}
		if(iLen==0) str="";
		else str=new String(bytes,0,iLen);
		return str;
	}
	
	public int    getSkt()			{ return Skt;					}
	public String getRemoteIP()		{ return bytes2Str(RemoteIP); 	}
	public int	  getRemotePort()	{ return RemotePort;			}
	public String getMyLocalIP()	{ return bytes2Str(MyLocalIP); 	}
	public int	  getMyLocalPort()	{ return MyLocalPort;			}
	public String getMyWanIP()		{ return bytes2Str(MyWanIP); 	}
	public int	  getMyWanPort()	{ return MyWanPort;				}
	
	public int 	  getConnectTime()	{ return ConnectTime;			}
	public String getDID()			{ return bytes2Str(DID);		}
	public int 	  getCorD()			{ return bCorD&0xFF;		}
	public int	  getMode()			{ return bMode&0xFF;		}
}
