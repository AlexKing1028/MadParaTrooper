package network;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ningchen Wang on 6/11/2017.
 */
public class PeerDetector {
	private PeerDetectorServer peerDetectorServer;
	public PeerDetector() {		
	}

	/**
	 *
	 * @param id
	 * @return -1:failure; 1:create a hotspot; 2:connect to wifi; 3:already in wifi
	 * @throws SocketException
	 */
    public int Initialize(int id) throws SocketException {
        // C++ WLAN Module Initialize
		int result = WLANDetectorWrapper.StartUpDetect(id);
		if (result < 0){
        	return -1;
        }
		int ifIndex = WLANDetectorWrapper.GetLocalAddress();
		NetworkInterface intf = NetworkInterface.getByIndex(ifIndex);
		System.out.println(intf.getName() + ':' + intf.getDisplayName() + ' ' + 
				intf.getIndex() + ' ' + intf.supportsMulticast());
		List<InterfaceAddress> iNetAddresses = intf.getInterfaceAddresses();
		for (InterfaceAddress n : iNetAddresses) {
			if (n.getAddress() instanceof Inet4Address) {
				System.out.println(n.getBroadcast().toString() + ' ' + n.getNetworkPrefixLength());
				peerDetectorServer = new PeerDetectorServer((Inet4Address)n.getAddress(), (short)24, id);
				peerDetectorServer.start();
				break;
			}
		}
        return result;
    }
    
    public int Stop() {
    	peerDetectorServer.kill();
    	WLANDetectorWrapper.StopDetect();
		return 0;
	}

    public Inet4Address GetLocalAddress() {
    	return peerDetectorServer.GetLocalAddress();
    }
    
    public Inet4Address[] GetPeerAddresses() {
    	return peerDetectorServer.GetPeerAddresses();
    }
    
    public HashMap<Inet4Address, Integer> GetPeerIDMap() {
    	return peerDetectorServer.GetPeerIDMap();
    }
}
