package net.transespdiscord.funciones.comandos;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Genericos {
    public static void hola(SlashCommandEvent evento) throws Exception {
        evento.reply("Hola " + evento.getMember().getAsMention() + ". ¡Que tengas un buen día!").queue();
    }
}
