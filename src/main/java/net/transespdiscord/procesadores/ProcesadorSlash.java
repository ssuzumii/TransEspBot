package net.transespdiscord.procesadores;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.transespdiscord.funciones.comandos.Administrativos;
import net.transespdiscord.funciones.comandos.Genericos;
import net.transespdiscord.funciones.comandos.Sistema;
import net.transespdiscord.funciones.comandos.Utilidades;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class ProcesadorSlash extends ListenerAdapter {

    public static void cargarComandos(JDA jda){
        CommandListUpdateAction comandos = jda.getGuilds().get(0).updateCommands();

        comandos.addCommands(new CommandData("hola", "Butterfree te saluda."));

        comandos.addCommands(new CommandData("latencia", "Indica cuánto tarda Butterfree en hablar con Discord."));

        SubcommandData crearTemporizador = new SubcommandData("crear", "Crea un temporizador.")
                .addOptions(new OptionData(INTEGER, "cantidad", "La cantidad de tiempo en la que quieres que te avise.").setRequired(true),
                        new OptionData(STRING, "unidad", "La unidad de tiempo de la cantidad especificada: s (segundos), m (minutos), h (horas) o d (días).").setRequired(true),
                        new OptionData(STRING, "nombre", "El nombre del temporizador. Máximo 30 caracteres."));

        SubcommandData consultarTemporizador = new SubcommandData("consultar", "Consulta tus temporizadores activos");

        SubcommandData eliminarTemporizador = new SubcommandData("eliminar", "Elimina un temporizador.")
                .addOptions(new OptionData(INTEGER, "id", "El identificador del temporizador que quieres eliminar. "
                + "Consíguelo con el subcomando consultar.").setRequired(true));

        comandos.addCommands(
                new CommandData("temporizador", "Haz que Butterfree te avise cuando pase el tiempo que le digas")
                        .addSubcommands(crearTemporizador).addSubcommands(consultarTemporizador).addSubcommands(eliminarTemporizador));

        comandos.addCommands(new CommandData("purgar", "Solo puede usarlo el Equipo Administrativo. "
                + "Elimina mensajes en lote.")
                .addOptions(new OptionData(INTEGER, "cantidad", "La cantidad de mensajes a eliminar.")
                        .setRequired(true)
                        .setRequiredRange(1, 30))
                .addOptions(new OptionData(USER, "usuarie", "Si se especifica, borra mensajes de ese usuarie en concreto.")));

        comandos.addCommands(new CommandData("bienvenide", "Solo puede usarlo el Equipo Administrativo. "
                + "Da la bienvenida a alguien al servidor.")
                .addOptions(new OptionData(USER, "usuarie", "La persona a quien dar la bienvenida.")
                        .setRequired(true))
                .addOptions(new OptionData(STRING, "pronombre", "Terminación de las palabras. Máx. 4 caracteres. Si no lo especificas, depende de los roles.")));

        comandos.queue();

    }

    @Override
    public void onSlashCommand(SlashCommandEvent evento){
        if (evento.getGuild() == null) {
            return;
        }

        try {
            if (evento.getMember().getRoles().isEmpty()) {
                evento.reply("Error: solo las personas admitidas en el servidor pueden utilizar mis comandos.").setEphemeral(true).queue();
                return;
            }
        } catch (NullPointerException e) {
            evento.reply("NullPointerException en comprobación de roles vacíos. Contacta con la desarrolladora.").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        }

        switch(evento.getName()){
            case "hola":
                Genericos.hola(evento);
                break;
            case "latencia":
                Sistema.latencia(evento);
                break;
            case "temporizador":
                if (evento.getSubcommandName() != null){
                    switch (evento.getSubcommandName()){
                        case "crear":
                            Utilidades.crearTemporizador(evento);
                            break;
                        case "consultar":
                            Utilidades.consultarTemporizador(evento);
                            break;
                        case "eliminar":
                            Utilidades.eliminarTemporizador(evento);
                            break;
                        default:
                            evento.reply("Ese subcomando no existe.").setEphemeral(true);
                    }
                } else {
                    evento.reply("Debes proporcionar un subcomando (crear, consultar o eliminar).").setEphemeral(true).queue();
                }

                break;
            case "purgar":
                Administrativos.purgar(evento);
                break;
            case "bienvenide":
                Administrativos.bienvenide(evento);
                break;
            default:
                evento.reply("Ese comando no existe.").setEphemeral(true).queue();
        }
    }


}
