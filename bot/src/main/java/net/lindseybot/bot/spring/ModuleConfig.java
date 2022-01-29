package net.lindseybot.bot.spring;

import net.lindseybot.automod.LibraryAutoMod;
import net.lindseybot.economy.LibraryEconomy;
import net.lindseybot.fun.LibraryFun;
import net.lindseybot.help.LibraryHelp;
import net.lindseybot.info.LibraryInfo;
import net.lindseybot.moderation.LibraryModeration;
import net.lindseybot.nsfw.LibraryNSFW;
import net.lindseybot.testing.LibraryTesting;
import net.lindseybot.wiki.LibraryWiki;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        LibraryAutoMod.class,
        LibraryEconomy.class,
        LibraryFun.class,
        LibraryHelp.class,
        LibraryInfo.class,
        LibraryModeration.class,
        LibraryNSFW.class,
        LibraryWiki.class,
        LibraryTesting.class
})
public class ModuleConfig {
}
