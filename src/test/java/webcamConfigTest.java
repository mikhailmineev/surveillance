import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class webcamConfigTest {

    private String output = "ffmpeg version 4.4-tessus  https://evermeet.cx/ffmpeg/  Copyright (c) 2000-2021 the FFmpeg developers\n" +
            "  built with Apple clang version 11.0.0 (clang-1100.0.33.17)\n" +
            "  configuration: --cc=/usr/bin/clang --prefix=/opt/ffmpeg --extra-version=tessus --enable-avisynth --enable-fontconfig --enable-gpl --enable-libaom --enable-libass --enable-libbluray --enable-libdav1d --enable-libfreetype --enable-libgsm --enable-libmodplug --enable-libmp3lame --enable-libmysofa --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenh264 --enable-libopenjpeg --enable-libopus --enable-librubberband --enable-libshine --enable-libsnappy --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvmaf --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-libzimg --enable-libzmq --enable-libzvbi --enable-version3 --pkg-config-flags=--static --disable-ffplay\n" +
            "  libavutil      56. 70.100 / 56. 70.100\n" +
            "  libavcodec     58.134.100 / 58.134.100\n" +
            "  libavformat    58. 76.100 / 58. 76.100\n" +
            "  libavdevice    58. 13.100 / 58. 13.100\n" +
            "  libavfilter     7.110.100 /  7.110.100\n" +
            "  libswscale      5.  9.100 /  5.  9.100\n" +
            "  libswresample   3.  9.100 /  3.  9.100\n" +
            "  libpostproc    55.  9.100 / 55.  9.100\n" +
            "[AVFoundation indev @ 0x7f8ff1606400] AVFoundation video devices:\n" +
            "[AVFoundation indev @ 0x7f8ff1606400] [0] FaceTime HD Camera\n" +
            "[AVFoundation indev @ 0x7f8ff1606400] [1] Capture screen 0\n" +
            "[AVFoundation indev @ 0x7f8ff1606400] AVFoundation audio devices:\n" +
            "[AVFoundation indev @ 0x7f8ff1606400] [0] Built-in Microphone\n" +
            ": Input/output error";

    @Test
    public void testParseMac() {
        var lines = Arrays.asList(output.split("\n"));
        var filtered = lines.stream().dropWhile(e -> !e.contains("AVFoundation video devices:"));
        log.info(filtered.collect(Collectors.joining()));
    }
}
