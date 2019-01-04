package tr.com.msen.ytu.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import demo.pkcs.pkcs11.wrapper.util.Util;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.AESSecretKey;
import iaik.pkcs.pkcs11.objects.Attribute;
import iaik.pkcs.pkcs11.objects.BooleanAttribute;
import iaik.pkcs.pkcs11.objects.GenericTemplate;
import iaik.pkcs.pkcs11.objects.Object;
import iaik.pkcs.pkcs11.parameters.InitializationVectorParameters;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;

public class YtuTestApp {

	static PrintWriter output_;
	static BufferedReader input_;

	static {
		try {
			output_ = new PrintWriter(System.out, true);
			input_ = new BufferedReader(new InputStreamReader(System.in));
		} catch (Throwable thr) {
			thr.printStackTrace();
			output_ = new PrintWriter(System.out, true);
			input_ = new BufferedReader(new InputStreamReader(System.in));
		}
	}

	public static void main(String[] args) throws TokenException, IOException, DecoderException {
		System.out.println("Hello world!");

		Module pkcs11Module = Module.getInstance("etpkcs11.dll");
		pkcs11Module.initialize(null);

		Slot[] slots = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
		Slot selectedSlot = slots[0];

		Token token = selectedSlot.getToken();

		Session session = Util.openAuthorizedSession(token, Token.SessionReadWriteBehavior.RW_SESSION, output_, input_,
				"1234");
		
		
		
		AESSecretKey encryptionKey = getSecretKey(session);
		
		
		String hex = Files.readAllLines(Paths.get("outputHex.txt")).get(0);
//		System.out.println(hex);
		byte[] encryptedData = Hex.decodeHex(hex);
		byte[] origValue = test(session, encryptionKey, encryptedData);
		
		Files.write(Paths.get("NLP.pptx"), origValue);
		

		System.out.println(encryptionKey);

	}

	private static byte[] test(Session session,AESSecretKey encryptionKey,byte[] encryptedData) throws TokenException {
		Mechanism decryptionMechanism = Mechanism.get(PKCS11Constants.CKM_AES_CBC_PAD);
		byte[] decryptInitializationVector = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		InitializationVectorParameters decryptInitializationVectorParameters = new InitializationVectorParameters(
				decryptInitializationVector);
		decryptionMechanism.setParameters(decryptInitializationVectorParameters);

		// initialize for decryption
		session.decryptInit(decryptionMechanism, encryptionKey);
		 
	    byte[] decryptedData = session.decrypt(encryptedData);
	    return decryptedData;
	}

	private static AESSecretKey getSecretKey(Session session) throws TokenException {
		GenericTemplate signatureKeyTemplate = new GenericTemplate();
		BooleanAttribute signAttribute = new BooleanAttribute(Attribute.SIGN);
		signAttribute.setBooleanValue(Boolean.TRUE);
		signatureKeyTemplate.addAttribute(signAttribute);

		// this find operation will find all objects that posess a CKA_SIGN
		// attribute with value true
		session.findObjectsInit(signatureKeyTemplate);

		Object[] foundSignatureKeyObjects = session.findObjects(1); // find
																	// first

		return (AESSecretKey) foundSignatureKeyObjects[0];

	}

}
