package gov.loc.repository.bagit.verify;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple task to check if a file exists on the filesystem. This is thread safe, so many can be called at once.
 */
@SuppressWarnings(value = {"PMD.DoNotUseThreads"})
public class CheckIfFileExistsTask implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(CheckIfFileExistsTask.class);
  private transient final Path file;
  private transient final List<Path> missingFiles;
  private transient final CountDownLatch latch;
  
  public CheckIfFileExistsTask(final Path file, final List<Path> missingFiles, final CountDownLatch latch) {
    this.file = file;
    this.latch = latch;
    this.missingFiles = missingFiles;
  }

  @Override
  public void run() {
    if(!Files.exists(file) && !existsNormalized()){
      logger.error("File [{}] does not exist!", file);
      missingFiles.add(file);
    }
    latch.countDown();
  }
  
  private boolean existsNormalized(){
    for(Normalizer.Form form : Normalizer.Form.values()){
      final String normalizedName = Normalizer.normalize(file.toString(), form);
      if(Files.exists(Paths.get(normalizedName))){
        logger.warn("File [{}] had to be normalized to [{}]. Consider fixing manifest so that it uses the same "
            + "form as the filesystem.", file, form);
        return true;
      }
    }
    
    return false;
  }
}
