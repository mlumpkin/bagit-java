package gov.loc.repository.bagit.hash;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.exceptions.UnsupportedAlgorithmException;

public abstract class AbstractMessageDigestHasher implements Hasher {
  private static final Logger logger = LoggerFactory.getLogger(SHA1Hasher.class);
  protected MessageDigest messageDigest;
  protected final String bagitName;
  protected final String messageDigestName;
  
  public AbstractMessageDigestHasher(final String messageDigestName, final String bagitName) throws NoSuchAlgorithmException {
    this.messageDigestName = messageDigestName;
    this.bagitName = bagitName;
    messageDigest = MessageDigest.getInstance(messageDigestName);
  }

  @Override
  public void update(final byte[] buffer, final int length) {
    messageDigest.update(buffer, 0, length);
  }

  @Override
  public void clear() {
    try {
      messageDigest = MessageDigest.getInstance(messageDigestName);
    } catch (NoSuchAlgorithmException e) {
      logger.error("Could not get a new instance of the {} message digest", messageDigestName, e);
    }
  }

  @Override
  public String getCalculatedValue() {
    final Formatter formatter = new Formatter();
    
    for (final byte b : messageDigest.digest()) {
      formatter.format("%02x", b);
    }
    
    final String hash = formatter.toString();
    formatter.close();
    
    return hash;
  }

  @Override
  public String getBagitName(){
    return bagitName;
  }
  
  @Override
  public Hasher instanceOf() throws UnsupportedAlgorithmException{
    try {
      return this.getClass().getConstructor().newInstance();
    } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | 
        InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new UnsupportedAlgorithmException(e);
    }
  }

  public MessageDigest getMessageDigest() {
    return messageDigest;
  }

  public void setMessageDigest(final MessageDigest messageDigest) {
    this.messageDigest = messageDigest;
  }

  public String getMessageDigestName() {
    return messageDigestName;
  }
}