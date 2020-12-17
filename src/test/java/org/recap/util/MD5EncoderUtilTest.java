package org.recap.util;

import org.junit.Test;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertTrue;

/**
 * Created by sheiks on 28/06/17.
 */
public class MD5EncoderUtilTest {

    @Test
    public void testEncode() throws NoSuchAlgorithmException {
        String word = "testWord";
        MD5EncoderUtil md5EncoderUtil = new MD5EncoderUtil();
        String md5EncodingString1 = md5EncoderUtil.getMD5EncodingString(word);
        System.out.println(md5EncodingString1);
        boolean recap = md5EncoderUtil.matching(md5EncodingString1, "testWord");

        assertTrue(recap);
    }

}
