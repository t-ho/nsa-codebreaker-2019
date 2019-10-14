package terrortimesol;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class CryptoHelper {

	public static Optional<PublicKey> convertPublicPEMtoPublicKey(String pemPublicKey)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		PemReader pemReader = new PemReader(new StringReader(pemPublicKey));
		PemObject pemPubKey = pemReader.readPemObject();
		pemReader.close();
		if (pemPubKey == null) {
			return Optional.ofNullable(null);
		}
		return Optional.ofNullable(KeyFactory.getInstance("RSA", new BouncyCastleProvider())
				.generatePublic(new X509EncodedKeySpec(pemPubKey.getContent())));
	}

}
