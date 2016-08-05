package org.keedio.flume.source.watchdir;

import org.keedio.flume.source.watchdir.listener.simpletxtsource.FileEventSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class CleanRemovedEventsProcessingThread implements Runnable {

  private FileEventSourceListener listener;
  private int seconds;
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CleanRemovedEventsProcessingThread.class);

  public int getProcessedEvents() {
    return processedEvents;
  }

  private int processedEvents = 0;

  public CleanRemovedEventsProcessingThread(FileEventSourceListener listener, int seconds) {
    this.listener = listener;
    this.seconds = seconds;
  }
  
  @Override
  public void run() {
    try {
      while (true) {
        process(listener.getFilesObserved());
        
        Thread.sleep(seconds * 1000);
      }
    } catch (Exception e) {
      LOGGER.debug("Error en la lectura del fichero, todavía no se ha generado."); 
    }
    
  }

  private void process(Map<String, InodeInfo> inodes) throws Exception {
    LOGGER.debug("Processing ");
      for (String inodeKey:inodes.keySet()) {

        try{
        InodeInfo inode = inodes.get(inodeKey);
        File file = new File(inode.getFileName());
        if (!file.exists()) {
          LOGGER.info("Removing inodekey '" + inodeKey + "' associated with file '"+file.getAbsolutePath()+"'");
          listener.getFilesObserved().remove(inodeKey);
        }
        } catch (Exception e) {
          LOGGER.info("Error procesando el listener", e);
        }
    }
  }

}