package org.asf.centuria.discord.handlers.discord.interactions.selectmenu;

import java.util.List;

import org.asf.centuria.discord.ServerConfigUtils;

import com.google.gson.JsonObject;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.component.SelectMenu.DefaultValue;
import discord4j.core.object.entity.Guild;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import reactor.core.publisher.Mono;

public class ServerConfigMenuHandler {

	/**
	 * Handles the 'server config' select menu event
	 * 
	 * @param event   Select menu event
	 * @param gateway Discord client
	 * @return Result Mono object
	 */
	public static Mono<?> handle(String id, SelectMenuInteractionEvent event, GatewayDiscordClient gateway) {
		// Find option and show results
		String option = event.getValues().get(0);
		switch (option) {
		case "modrole":
			return event.reply(roleSetting("moderatorRole", "Moderator Role",
					new String[] { "moderator", "mod", "centuria" }, event.getMessage().get().getGuild().block()));
		case "devrole":
			return event.reply(roleSetting("developerRole", "Developer Role",
					new String[] { "developer", "dev", "centuria" }, event.getMessage().get().getGuild().block()));
		case "announcementrole":
			return event.reply(roleSetting("announcementPingRole", "Announcement Ping Role",
					new String[] { "announcement", "ping", "centuria" }, event.getMessage().get().getGuild().block()));
		case "announcementchannel":
			return event.reply(channelSetting("announcementChannel", "Announcement Channel",
					new String[] { "announcement", "centuria" }, event.getMessage().get().getGuild().block()));
		case "reportchannel":
			return event.reply(channelSetting("reportChannel", "Member Report Review Channel",
					new String[] { "moderator", "moderation", "report", "review", "centuria" },
					event.getMessage().get().getGuild().block()));
		case "moderationlogchannel":
			return event.reply(channelSetting("moderationLogChannel", "Moderation Log Channel",
					new String[] { "moderator", "moderation", "log", "centuria" },
					event.getMessage().get().getGuild().block()));
		case "feedbackchannel":
			return event.reply(channelSetting("feedbackChannel", "Feedback Review Channel",
					new String[] { "developer", "development", "feedback", "review", "centuria" },
					event.getMessage().get().getGuild().block()));
		}

		// Default response
		return Mono.empty();
	}

	// Easy-access method for channels
	private static InteractionApplicationCommandCallbackSpec channelSetting(String configOptionName, String channelName,
			String[] filter, Guild guild) {
		// Find existing role
		String lastSelectedChannel = null;
		JsonObject obj = ServerConfigUtils.getServerConfig(guild.getId().asString());
		if (obj.has(configOptionName))
			lastSelectedChannel = obj.get(configOptionName).getAsString();

		// Build message
		InteractionApplicationCommandCallbackSpec.Builder msg = InteractionApplicationCommandCallbackSpec.builder();
		msg.content("Channel configuration: **" + channelName + "**");
		msg.addComponent(ActionRow.of(SelectMenu.ofChannel("serveroptionselection/" + configOptionName,
				List.of(DefaultValue.of(Snowflake.of(lastSelectedChannel), DefaultValue.Type.CHANNEL)))));

		// Return message
		return msg.ephemeral(true).build();
	}

	// Easy-access method for roles
	private static InteractionApplicationCommandCallbackSpec roleSetting(String configOptionName, String roleName,
			String[] filter, Guild guild) {
		// Find existing role
		String lastSelectedRole = null;
		JsonObject obj = ServerConfigUtils.getServerConfig(guild.getId().asString());
		if (obj.has(configOptionName))
			lastSelectedRole = obj.get(configOptionName).getAsString();

		// Build message
		InteractionApplicationCommandCallbackSpec.Builder msg = InteractionApplicationCommandCallbackSpec.builder();
		msg.content("Role configuration: **" + roleName + "**");
		msg.addComponent(ActionRow.of(SelectMenu.ofRole("serveroptionselection/" + configOptionName,
				List.of(DefaultValue.of(Snowflake.of(lastSelectedRole), DefaultValue.Type.ROLE)))));

		// Return message
		return msg.ephemeral(true).build();
	}
}
