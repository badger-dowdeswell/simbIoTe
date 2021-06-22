//
// NON-BLOCKING SERVER AND CLIENT STATUS AND ERROR CODES
// =====================================================
// The status and error codes returned by the functions in
// Non-Blocking TCP/IP server and client classes.
//
// <RA_BRD - 
package HVACsim; // <-RA_BRD: Package needs to become generic so it can be moved into a production library.

public class NIOstatus {
	public static final int UNDEFINED = 0;
	public static final int EXIT_SUCCESS = 1;
	public static final int EXIT_FAILURE = -1;
	public static final int INVALID_HOST_NAME = 100;
	public static final int INVALID_LISTENER_PORT = 101;
}
