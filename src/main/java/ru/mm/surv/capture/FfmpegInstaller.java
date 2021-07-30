package ru.mm.surv.capture;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

@Component
@Slf4j
public class FfmpegInstaller {

    public static final String FFMPEG_RESOURCES_PATH = "classpath:/bin/ffmpeg";

    private final Path executableFolder;

    private final Path ffmpeg;

    @Autowired
    public FfmpegInstaller(@Value("${ffmpeg.folder}") Path executableFolder) {
        this.executableFolder = executableFolder;
        this.ffmpeg = installExecutable();
    }

    public Path getPath() {
        return ffmpeg;
    }

    private Path installExecutable() {
        ConsumerIO<Path> postActions;
        String os;

        if (SystemUtils.IS_OS_WINDOWS) {
            os = "win";
            postActions = (path) -> {};
        } else if (SystemUtils.IS_OS_MAC) {
            os = "mac";
            postActions = (path) -> {
                Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                Files.setPosixFilePermissions(path, perms);
            };
        } else {
            throw new RuntimeException("Only Windows, MacOS supported");
        }

        Resource ffmpegResource;
        try {
            log.info("Ffmpeg not installed, installing new");
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources( FFMPEG_RESOURCES_PATH + "/" + os + "/*");
            if (resources.length != 1) {
                throw new RuntimeException("Failed to locate ffmpeg in distribution path");
            }
            ffmpegResource = resources[0];
        } catch (IOException e) {
            throw new RuntimeException("Failed to locate ffmpeg in distribution path", e);
        }

        String ffmpegBinName = ffmpegResource.getFilename();
        Path target = executableFolder.resolve(ffmpegBinName);
        if (Files.exists(target)) {
            return target;
        }

        try {
            Files.createDirectories(executableFolder);
            try (InputStream inputStream = ffmpegResource.getInputStream();
                OutputStream outputStream = new FileOutputStream(target.toFile())) {
                inputStream.transferTo(outputStream);
            }
            postActions.accept(target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to install ffmpeg", e);
        }

        return target;
    }
}
