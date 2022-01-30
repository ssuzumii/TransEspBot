package net.transespdiscord.funciones.comandos;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Sistema {
    public static void latencia(SlashCommandEvent evento){
        evento.reply("Estoy tardando " + evento.getJDA().getGatewayPing() + "ms en comunicarme con Discord.").queue();
    }
}
