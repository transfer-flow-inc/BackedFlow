package fr.nil.backedflow.jobs;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.services.folder.FolderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;


@Data
@RequiredArgsConstructor
@Slf4j
public class DailyTaskJob implements Job {

    private final FolderService folderService;

    @Override
    public void execute(JobExecutionContext context) {

        List<Folder> expiredFolders = folderService.getExpiredFolders();
        log.info(String.format("Deleting %d expired folders", expiredFolders.size()));
        expiredFolders.forEach(folder -> {
            folderService.deleteFolder(folder);
        });


    }
}
