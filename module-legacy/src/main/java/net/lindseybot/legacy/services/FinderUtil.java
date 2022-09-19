package net.lindseybot.legacy.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class FinderUtil {

    private final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}"); // ID
    private final static Pattern FULL_USER_REF = Pattern.compile("(.{2,32})\\s*#(\\d{4})"); // $1 -> username, $2 -> discriminator
    private final static Pattern USER_MENTION = Pattern.compile("<@!?(\\d{17,20})>"); // $1 -> ID
    private final static Pattern CHANNEL_MENTION = Pattern.compile("<#(\\d{17,20})>"); // $1 -> ID
    private final static Pattern ROLE_MENTION = Pattern.compile("<@&(\\d{17,20})>"); // $1 -> ID

    // Prevent instantiation
    private FinderUtil() {
    }

    /**
     * Queries a provided Guild Guild} for TextChannel
     * TextChannels.
     *
     * <p>The following special case is applied before the standard search is done:
     * <ul>
     * <li>Channel Mention: Query provided matches a #channel mention (more specifically {@literal <#channelID>})</li>
     * </ul>
     *
     * @param rawQuery The String query to search by
     * @param guild    The Guild to search from
     * @return A possibly-empty {@link List List} of TextChannels found by the query from the provided Guild.
     */
    public static List<TextChannel> findTextChannels(String rawQuery, Guild guild) {
        Matcher channelMention = CHANNEL_MENTION.matcher(rawQuery);
        if (channelMention.matches()) {
            TextChannel tc = guild.getTextChannelById(channelMention.replaceAll("$1"));
            if (tc != null)
                return Collections.singletonList(tc);
        } else if (DISCORD_ID.matcher(rawQuery).matches()) {
            TextChannel tc = guild.getTextChannelById(rawQuery);
            if (tc != null)
                return Collections.singletonList(tc);
        }
        ArrayList<TextChannel> exact = new ArrayList<>();
        ArrayList<TextChannel> wrongcase = new ArrayList<>();
        ArrayList<TextChannel> startswith = new ArrayList<>();
        ArrayList<TextChannel> contains = new ArrayList<>();
        if (rawQuery.startsWith("#")) {
            rawQuery = rawQuery.replaceFirst("#", "");
        }
        String query = rawQuery; // So it's effectively final
        String lowerquery = query.toLowerCase();
        guild.getTextChannelCache().forEach((tc) -> {
            String name = tc.getName();
            if (name.equals(query))
                exact.add(tc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongcase.add(tc);
            else if (name.toLowerCase().startsWith(lowerquery) && wrongcase.isEmpty())
                startswith.add(tc);
            else if (name.toLowerCase().contains(lowerquery) && startswith.isEmpty())
                contains.add(tc);
        });
        if (!exact.isEmpty())
            return exact;
        if (!wrongcase.isEmpty())
            return wrongcase;
        if (!startswith.isEmpty())
            return startswith;
        return contains;
    }

    /**
     * Queries a provided instance of JDA for VoiceChannel
     * VoiceChannels.
     *
     * <p>The standard search does not follow any special cases.
     *
     * @param query The String query to search by
     * @param jda   The instance of JDA to search from
     * @return A possibly-empty {@link List List} of VoiceChannels found by the query from the provided JDA instance.
     */
    public static List<VoiceChannel> findVoiceChannels(String query, JDA jda) {
        if (DISCORD_ID.matcher(query).matches()) {
            VoiceChannel vc = jda.getVoiceChannelById(query);
            if (vc != null)
                return Collections.singletonList(vc);
        }
        ArrayList<VoiceChannel> exact = new ArrayList<>();
        ArrayList<VoiceChannel> wrongcase = new ArrayList<>();
        ArrayList<VoiceChannel> startswith = new ArrayList<>();
        ArrayList<VoiceChannel> contains = new ArrayList<>();
        String lowerquery = query.toLowerCase();
        jda.getVoiceChannelCache().forEach((vc) -> {
            String name = vc.getName();
            if (name.equals(query))
                exact.add(vc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongcase.add(vc);
            else if (name.toLowerCase().startsWith(lowerquery) && wrongcase.isEmpty())
                startswith.add(vc);
            else if (name.toLowerCase().contains(lowerquery) && startswith.isEmpty())
                contains.add(vc);
        });
        if (!exact.isEmpty())
            return exact;
        if (!wrongcase.isEmpty())
            return wrongcase;
        if (!startswith.isEmpty())
            return startswith;
        return contains;
    }

    /**
     * Queries a provided Guild Guild} for VoiceChannel
     * VoiceChannels.
     *
     * <p>The standard search does not follow any special cases.
     *
     * @param query The String query to search by
     * @param guild The Guild to search from
     * @return A possibly-empty {@link List List} of VoiceChannels found by the query from the provided Guild.
     */
    public static List<VoiceChannel> findVoiceChannels(String query, Guild guild) {
        if (DISCORD_ID.matcher(query).matches()) {
            VoiceChannel vc = guild.getVoiceChannelById(query);
            if (vc != null)
                return Collections.singletonList(vc);
        }
        ArrayList<VoiceChannel> exact = new ArrayList<>();
        ArrayList<VoiceChannel> wrongcase = new ArrayList<>();
        ArrayList<VoiceChannel> startswith = new ArrayList<>();
        ArrayList<VoiceChannel> contains = new ArrayList<>();
        String lowerquery = query.toLowerCase();
        guild.getVoiceChannels().forEach((vc) -> {
            String name = vc.getName();
            if (name.equals(query))
                exact.add(vc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongcase.add(vc);
            else if (name.toLowerCase().startsWith(lowerquery) && wrongcase.isEmpty())
                startswith.add(vc);
            else if (name.toLowerCase().contains(lowerquery) && startswith.isEmpty())
                contains.add(vc);
        });
        if (!exact.isEmpty())
            return exact;
        if (!wrongcase.isEmpty())
            return wrongcase;
        if (!startswith.isEmpty())
            return startswith;
        return contains;
    }

    /**
     * Queries a provided Guild Guild} for Role Roles.
     *
     * <p>The following special case is applied in order of listing before the standard search is done:
     * <ul>
     * <li>Role Mention: Query provided matches a @role mention (more specifically {@literal <@&roleID>})</li>
     * </ul>
     *
     * @param query The String query to search by
     * @param guild The Guild to search from
     * @return A possibly-empty {@link List List} of Roles found by the query from the provided Guild.
     */
    public static List<Role> findRoles(String query, Guild guild) {
        Matcher roleMention = ROLE_MENTION.matcher(query);
        if (roleMention.matches()) {
            Role role = guild.getRoleById(roleMention.group(1));
            if (role != null)
                return Collections.singletonList(role);
        } else if (DISCORD_ID.matcher(query).matches()) {
            Role role = guild.getRoleById(query);
            if (role != null)
                return Collections.singletonList(role);
        } else if (query.equalsIgnoreCase("@everyone") || query.equalsIgnoreCase("everyone")) {
            return Collections.singletonList(guild.getPublicRole());
        }
        ArrayList<Role> exact = new ArrayList<>();
        ArrayList<Role> wrongcase = new ArrayList<>();
        ArrayList<Role> startswith = new ArrayList<>();
        ArrayList<Role> contains = new ArrayList<>();
        if (query.startsWith("@")) {
            query = query.replaceFirst("@", "");
        }
        String finalQuery = query; // So it's effectively final
        String lowerquery = finalQuery.toLowerCase();
        List<Role> guildRoles = new ArrayList<>(guild.getRoles());
        guildRoles.add(guild.getPublicRole());
        guildRoles.forEach((role) -> {
            String name = role.getName();
            if (name.equals(finalQuery))
                exact.add(role);
            else if (name.equalsIgnoreCase(finalQuery) && exact.isEmpty())
                wrongcase.add(role);
            else if (name.toLowerCase().startsWith(lowerquery) && wrongcase.isEmpty())
                startswith.add(role);
            else if (name.toLowerCase().contains(lowerquery) && startswith.isEmpty())
                contains.add(role);
        });
        if (!exact.isEmpty())
            return exact;
        if (!wrongcase.isEmpty())
            return wrongcase;
        if (!startswith.isEmpty())
            return startswith;
        return contains;
    }

    public static Optional<TextChannel> findTextChannel(String query, Guild guild) {
        List<TextChannel> channels = findTextChannels(query, guild);
        return channels.isEmpty() ? Optional.empty() : Optional.of(channels.get(0));
    }

    public static Role findRole(String query, Guild guild) {
        List<Role> roles = findRoles(query, guild);
        return roles.isEmpty() ? null : roles.get(0);
    }

    public static Member findMember(String query, Guild guild) {
        Matcher userMention = USER_MENTION.matcher(query);
        if (userMention.matches()) {
            return guild.retrieveMemberById(userMention.replaceAll("$1"))
                    .complete();
        }
        // Id
        if (DISCORD_ID.matcher(query).matches()) {
            return guild.retrieveMemberById(query)
                    .complete();
        }
        // User#Dis
        Matcher fullRefMatch = FULL_USER_REF.matcher(query);
        if (fullRefMatch.matches()) {
            String name = fullRefMatch.replaceAll("$1");
            String disc = fullRefMatch.replaceAll("$2");
            List<Member> oneMember = guild.retrieveMembersByPrefix(name, 1).get();
            if (oneMember.isEmpty()) {
                return null;
            }
            Member member = oneMember.get(0);
            if (member.getUser().getDiscriminator().equals(disc)) {
                return member;
            } else {
                return null;
            }
        }
        // -- Fuzzy
        if (query.startsWith("@")) {
            query = query.replaceFirst("@", "");
        }
        List<Member> members = guild.retrieveMembersByPrefix(query, 10).get();
        // --
        int bestScore = 0;
        Member bestMember = null;
        for (Member member : members) {
            int score = 0;
            String currentName = member.getEffectiveName();
            if (currentName.equals(query)) {
                score += 4;
            } else if (currentName.equalsIgnoreCase(query)) {
                score += 3;
            } else if (currentName.startsWith(query)) {
                score += 2;
            } else if (currentName.contains(query)) {
                score++;
            }
            if (score > bestScore) {
                bestMember = member;
                bestScore = score;
            }
        }
        return bestMember;
    }

}
