
package tr.com.msen.ytu.crypto;

import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.AESSecretKey;
import iaik.pkcs.pkcs11.objects.Attribute;
import iaik.pkcs.pkcs11.objects.BooleanAttribute;
import iaik.pkcs.pkcs11.objects.GenericTemplate;
import iaik.pkcs.pkcs11.objects.Object;

/**
 * This class demonstrates how to use the GenericSearchTemplate class.
 */
public class FindSignatureKey {

	public static AESSecretKey getSecretKey(Session session) throws TokenException {
		GenericTemplate signatureKeyTemplate = new GenericTemplate();
		BooleanAttribute signAttribute = new BooleanAttribute(Attribute.SIGN);
		signAttribute.setBooleanValue(Boolean.TRUE);
		signatureKeyTemplate.addAttribute(signAttribute);

		// this find operation will find all objects that posess a CKA_SIGN
		// attribute with value true
		session.findObjectsInit(signatureKeyTemplate);

		Object[] foundSignatureKeyObjects = session.findObjects(1);
		session.findObjectsFinal();
		return (AESSecretKey)foundSignatureKeyObjects[0];
	}
	
}
