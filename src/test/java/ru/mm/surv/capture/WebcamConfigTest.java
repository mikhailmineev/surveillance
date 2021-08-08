package ru.mm.surv.capture;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mm.surv.capture.config.InputSource;
import ru.mm.surv.capture.config.InputType;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.service.FfmpegInstaller;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


@Slf4j
public class WebcamConfigTest {

    private String outputMac = "ffmpeg version 4.4-tessus  https://evermeet.cx/ffmpeg/  Copyright (c) 2000-2021 the FFmpeg developers\n" +
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

    private String outputWin = "ffmpeg version N-103050-g69aa2488fc-20210723 Copyright (c) 2000-2021 the FFmpeg developers\n" +
            "  built with gcc 10-win32 (GCC) 20210408\n" +
            "  configuration: --prefix=/ffbuild/prefix --pkg-config-flags=--static --pkg-config=pkg-config --cross-prefix=x86_64-w64-mingw32- --arch=x86_64 --target-os=mingw32 --enable-gpl --enable-version3 --disable-debug --disable-w32threads --enable-pthreads --enable-iconv --enable-libxml2 --enable-zlib --enable-libfreetype --enable-libfribidi --enable-gmp --enable-lzma --enable-fontconfig --enable-libvorbis --enable-opencl --enable-libvmaf --enable-vulkan --disable-libxcb --disable-xlib --enable-amf --enable-libaom --enable-avisynth --enable-libdav1d --enable-libdavs2 --disable-libfdk-aac --enable-ffnvcodec --enable-cuda-llvm --enable-libglslang --enable-libgme --enable-libass --enable-libbluray --enable-libmp3lame --enable-libopus --enable-libtheora --enable-libvpx --enable-libwebp --enable-lv2 --enable-libmfx --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-librav1e --enable-librubberband --enable-schannel --enable-sdl2 --enable-libsoxr --enable-libsrt --enable-libsvtav1 --enable-libtwolame --enable-libuavs3d --disable-libdrm --disable-vaapi --enable-libvidstab --enable-libx264 --enable-libx265 --enable-libxavs2 --enable-libxvid --enable-libzimg --extra-cflags=-DLIBTWOLAME_STATIC --extra-cxxflags= --extra-ldflags=-pthread --extra-ldexeflags= --extra-libs=-lgomp --extra-version=20210723\n" +
            "  libavutil      57.  1.100 / 57.  1.100\n" +
            "  libavcodec     59.  3.102 / 59.  3.102\n" +
            "  libavformat    59.  4.101 / 59.  4.101\n" +
            "  libavdevice    59.  0.100 / 59.  0.100\n" +
            "  libavfilter     8.  0.103 /  8.  0.103\n" +
            "  libswscale      6.  0.100 /  6.  0.100\n" +
            "  libswresample   4.  0.100 /  4.  0.100\n" +
            "  libpostproc    56.  0.100 / 56.  0.100\n" +
            "[dshow @ 0000029379f12ac0] DirectShow video devices (some may be both video and audio devices)\n" +
            "[dshow @ 0000029379f12ac0]  \"Genius Webcam\"\n" +
            "[dshow @ 0000029379f12ac0]     Alternative name \"@device_pnp_\\\\?\\usb#vid_0458&pid_6007&mi_00#6&3af731a&0&0000#{65e8773d-8f56-11d0-a3b9-00a0c9223196}\\global\"\n" +
            "[dshow @ 0000029379f12ac0]  \"USB Video Device\"\n" +
            "[dshow @ 0000029379f12ac0]     Alternative name \"@device_pnp_\\\\?\\usb#vid_046d&pid_081b&mi_00#6&1465dc72&0&0000#{65e8773d-8f56-11d0-a3b9-00a0c9223196}\\global\"\n" +
            "[dshow @ 0000029379f12ac0]  \"OBS Virtual Camera\"\n" +
            "[dshow @ 0000029379f12ac0]     Alternative name \"@device_sw_{860BB310-5D01-11D0-BD3B-00A0C911CE86}\\{A3FCE0F5-3493-419F-958A-ABA1250EC20B}\"\n" +
            "[dshow @ 0000029379f12ac0] DirectShow audio devices\n" +
            "[dshow @ 0000029379f12ac0]  \"Microphone (Genius Audio)\"\n" +
            "[dshow @ 0000029379f12ac0]     Alternative name \"@device_cm_{33D9A762-90C8-11D0-BD43-00A0C911CE86}\\wave_{493E85BB-F5B3-4B46-840E-4EAEE3902428}\"\n" +
            "[dshow @ 0000029379f12ac0]  \"Microphone (USB Audio Device)\"\n" +
            "[dshow @ 0000029379f12ac0]     Alternative name \"@device_cm_{33D9A762-90C8-11D0-BD43-00A0C911CE86}\\wave_{19A7A61B-8B9F-4A99-B816-EEA149BB61CC}\"\n" +
            "[dshow @ 0000029379f12ac0]  \"Headset Microphone (Oculus Virtual Audio Device)\"\n" +
            "[dshow @ 0000029379f12ac0]     Alternative name \"@device_cm_{33D9A762-90C8-11D0-BD43-00A0C911CE86}\\wave_{1C96D137-BCFC-4462-8470-5B2F70DA954F}\"\n" +
            "dummy: Immediate exit requested\n";

    private FfmpegInstaller ffmpegInstaller;
    private WebcamDiscovery webcamDiscovery;

    @BeforeEach
    public void init() {
        ffmpegInstaller = mock(FfmpegInstaller.class);
        webcamDiscovery = new WebcamDiscovery(ffmpegInstaller);
    }
    @Test
    public void testParseMac() {
        List<InputSource> sources = webcamDiscovery.parse(Platform.MACOS, outputMac);
        assertEquals(3, sources.size());
        assertEquals("0", sources.get(0).getId());
        Assertions.assertEquals(InputType.VIDEO, sources.get(0).getType());
        assertEquals("FaceTime HD Camera", sources.get(0).getName());
    }

    @Test
    public void testParseWin() {
        List<InputSource> sources = webcamDiscovery.parse(Platform.WIN, outputWin);
        assertEquals(6, sources.size());
        assertEquals("Genius Webcam", sources.get(0).getId());
        assertEquals(InputType.VIDEO, sources.get(0).getType());
        assertEquals("Genius Webcam", sources.get(0).getName());
    }

}
