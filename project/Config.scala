/*
 * Copyright (C) 2015-2016 Lymia Aluysia <lymiahugs@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

object Config {
  val config_scalaVersion = "2.11.8"

  val config_home_url     = "https://github.com/Lymia/MPPatch"

  val config_make         = "make"
  val config_mingw_prefix = "i686-w64-mingw32-"
  val config_mingw_gcc    = config_mingw_prefix+"gcc"
  val config_linux_gcc    = "gcc"
  val config_nasm         = "nasm"

  val config_win32_secureFlags  = Seq("-Wl,-Bstatic", "-lssp", "-Wl,--dynamicbase,--nxcompat")
  val config_linux_secureFlags  = Seq("-Wl,-z,now", "-Wl,-z,relro")
  val config_common_secureFlags = Seq("-fstack-protector", "-fstack-protector-strong", "-D_FORTIFY_SOURCE=2")

  val version_baseVersion  = "0.1.0"

  val config_steam_sdlbin_path = "libsdl2_2.0.3+steamrt1+srt4_i386.deb"
  val config_steam_sdldev_path = "libsdl2-dev_2.0.3+steamrt1+srt4_i386.deb"
  val config_steam_sdlbin_name = "libSDL2-2.0.so.0"

  // hardcoded mirror because rediection handling is :(
  val config_launch4j_url      = "https://pilotfiber.dl.sourceforge.net/project/launch4j/launch4j-3/3.9/launch4j-3.9-linux.tgz"
}
