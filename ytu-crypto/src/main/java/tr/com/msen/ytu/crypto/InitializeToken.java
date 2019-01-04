package tr.com.msen.ytu.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.TokenInfo;

public class InitializeToken {

	public static Session generateToken(final Module pkcs11Module,final String label) throws TokenException, IOException {
		Slot[] slots = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
		Slot selectedSlot = slots[0];

		Token token = selectedSlot.getToken();

		TokenInfo tokenInfo = token.getTokenInfo();

		if (tokenInfo.isProtectedAuthenticationPath()) {
			Files.write(Paths.get("error.txt"), "Token tipi uyumsuz!".getBytes());
			System.exit(1);
		}
		
		String soPINString = String.valueOf(new Random().ints(1000, 9999).findFirst().getAsInt());
		Files.write(Paths.get("pin.txt"), soPINString.getBytes());

		token.initToken(soPINString.toCharArray(), label);

		Session session = token.openSession(Token.SessionType.SERIAL_SESSION, Token.SessionReadWriteBehavior.RW_SESSION,
				null, null);

		session.login(Session.UserType.SO, soPINString.toCharArray());
		session.initPIN(soPINString.toCharArray());
		session.closeSession();
		
		session = token.openSession(Token.SessionType.SERIAL_SESSION, Token.SessionReadWriteBehavior.RW_SESSION,
				null, null);
		session.login(Session.UserType.USER, soPINString.toCharArray());
		
		return session;
		
	}
	
	
}
