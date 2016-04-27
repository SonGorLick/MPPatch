/*
 * Copyright (c) 2015-2016 Lymia Alusyia <lymia@lymiahugs.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package moe.lymia.multiverse.ui

import java.io.File
import java.nio.file.{Files, Path}
import java.util.Locale

import moe.lymia.multiverse.core.data._
import moe.lymia.multiverse.core.installer.Installer
import moe.lymia.multiverse.util.res.{I18N, VersionInfo}
import moe.lymia.multiverse.platform.Platform

case class CLIArguments(command: (CLIArguments, Platform) => Unit,
                        systemPath: Option[Path], userPath: Option[Path],
                        // common options
                        force: Boolean = false,
                        // patch options
                        debug: Boolean = false)

class CLI(locale: Locale) {
  private val i18n = I18N(locale)
  private val parser = new scopt.OptionParser[CLIArguments]("mvmm") {
    head("Multiverse Mod Manager", "v"+VersionInfo.versionString)

    help("help").text(i18n(s"cli.param.help"))

    private def cmd2(name: String) = {
      note("")
      cmd(name).text(i18n(s"cli.cmd.$name"))
    }

    opt[File]("system-path").action((f, args) => args.copy(systemPath = Some(f.toPath)))
      .valueName(i18n("cli.param.args.directory")).text(i18n("cli.param.system-path"))
    opt[File]("user-path"  ).action((f, args) => args.copy(userPath   = Some(f.toPath)))
      .valueName(i18n("cli.param.args.directory")).text(i18n("cli.param.user-path"  ))

    cmd2("status").action((_, args) => args.copy(command = cmd_status))
    cmd2("list").action((_, args) => args.copy(command = cmd_list))
    cmd2("updatePatch").action((_, args) => args.copy(command = cmd_update))
      .children(
        opt[Unit]('f', "force").action((_, args) => args.copy(force = true)).text(i18n("cli.param.updatePatch.force")),
        opt[Unit]('d', "debug").action((_, args) => args.copy(debug = true)).text(i18n("cli.param.updatePatch.debug"))
      )
  }

  private def resolvePaths(paths: Seq[Path]) = paths.find(x => Files.exists(x) && Files.isDirectory(x))
  private def loadInstaller(args: CLIArguments, platform: Platform) =
    new Installer(args.systemPath.get, args.userPath.get, platform)

  private def cmd_unknown(args: CLIArguments, platform: Platform) = {
    println(i18n("cli.cmd.unknown"))
  }
  private def cmd_status(args: CLIArguments, platform: Platform) = {
    val installer = loadInstaller(args, platform)
    println(i18n("cli.cmd.status.patchStatus", installer.patchInstaller.checkPatchStatus()))
  }
  private def cmd_list(args: CLIArguments, platform: Platform) = {
    val installer = loadInstaller(args, platform)

    def printManifestList[T <: ManifestCommon](entries: ManifestList[T]): Unit = {
      if(entries.manifestList.isEmpty) println(s"  - ${i18n("cli.cmd.list.noEntries")}")
      else for(entry <- entries.manifestList) {
        println(s"  - ${entry.manifest.uuid}: ${entry.manifest.name}")
      }
    }

    println(i18n("cli.cmd.list.mods"))
    printManifestList(installer.listMods())

    println(i18n("cli.cmd.list.dlc"))
    printManifestList(installer.listDLC())
  }
  private def cmd_update(args: CLIArguments, platform: Platform) = {
    val installer = loadInstaller(args, platform)
    if(!args.force) installer.patchInstaller.safeUpdate(args.debug)
    else sys.error("force update not yet implemented")
  }

  def executeCommand(args: Seq[String]) = {
    val platform = Platform.currentPlatform.getOrElse(sys.error("Unknown platform."))
    val systemPath = resolvePaths(platform.defaultSystemPaths)
    val userPath   = resolvePaths(platform.defaultUserPaths)
    parser.parse(args, CLIArguments(cmd_unknown, systemPath, userPath)) match {
      case Some(command) => command.command(command, platform)
      case None => System.exit(-1)
    }
  }
}