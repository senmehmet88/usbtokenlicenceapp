package tr.com.msen.ytu.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.TokenInfo;

public class InitializeTokenTest {

	static PrintWriter output_;

	static BufferedReader input_;

	static {
		try {
			// output_ = new PrintWriter(new FileWriter("GetInfo_output.txt"),
			// true);
			output_ = new PrintWriter(System.out, true);
			input_ = new BufferedReader(new InputStreamReader(System.in));
		} catch (Throwable thr) {
			thr.printStackTrace();
			output_ = new PrintWriter(System.out, true);
			input_ = new BufferedReader(new InputStreamReader(System.in));
		}
	}

	private static final String soPINString = "1234";
	private static final String label = "YTU CRYPTO";
	
	
	public static void main(String[] args) throws IOException, TokenException {

		Module pkcs11Module = Module.getInstance("etpkcs11.dll");
		pkcs11Module.initialize(null);

		Slot[] slots = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
		Slot selectedSlot = slots[0];

		Token token = selectedSlot.getToken();

		TokenInfo tokenInfo = token.getTokenInfo();

		if (tokenInfo.isProtectedAuthenticationPath()) {
			System.out.println("Pin pad uzerinden giris yapilmasi gerekiyor.");
			return;
		}

		token.initToken(soPINString.toCharArray(), label);

		Session session = token.openSession(Token.SessionType.SERIAL_SESSION, Token.SessionReadWriteBehavior.RW_SESSION,
				null, null);

		session.login(Session.UserType.SO, soPINString.toCharArray());
		session.initPIN(soPINString.toCharArray());
		session.closeSession();
		pkcs11Module.finalize(null);

	}

}
