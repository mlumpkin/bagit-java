package gov.loc.repository.bagit.v0_96;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.ManifestReader.FilenameFixity;

import org.junit.Test;


public class ManifestReaderImplTest {

	
	
	@Test
	public void test() throws Exception {
		BagPartFactory factory = BagFactory.getBagPartFactory(Version.V0_96);
		String manifest = 
			"8ad8757baa8564dc136c1e07507f4a98 data/test1.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98	data/test3.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98	data/test 4.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98  data/test5.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98 *data/test6.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98__data/test7.txt\n";
		
		ManifestReader reader = factory.createManifestReader(new ByteArrayInputStream(manifest.getBytes("utf-8")), "utf-8");
		FilenameFixity ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test1.txt", ff.getFilename());
				
		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test3.txt", ff.getFilename());
		
		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test 4.txt", ff.getFilename());
		
		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test5.txt", ff.getFilename());

		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test6.txt", ff.getFilename());
		
		//Skip bad lines
		assertFalse(reader.hasNext());
		
		reader.close();

		
	}
	
	@Test(expected=RuntimeException.class)
	public void testFilenameWithBackslash() throws Exception {
		BagPartFactory factory = BagFactory.getBagPartFactory(Version.V0_96);
		String manifest = 
			"8ad8757baa8564dc136c1e07507f4a98 data/test2\\.txt\n";
		
		ManifestReader reader = factory.createManifestReader(new ByteArrayInputStream(manifest.getBytes("utf-8")), "utf-8");
		reader.next();
		
	}
	
}
