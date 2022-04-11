package net.transespdiscord.procesadores;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.transespdiscord.funciones.comandos.*;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

@Slf4j
public class ProcesadorSlash extends ListenerAdapter {
    public static void cargarComandos(JDA jda) {
        log.debug("Iniciando carga de la lista de comandos en Discord...");
        CommandListUpdateAction comandos = jda.getGuilds().get(0).updateCommands();

        comandos.addCommands(new CommandData("hola", "Butterfree te saluda."));

        comandos.addCommands(new CommandData("latencia", "Indica cuánto tarda Butterfree en hablar con Discord."));

        SubcommandData crearTemporizador = new SubcommandData("crear", "Crea un temporizador.")
                .addOptions(new OptionData(INTEGER, "cantidad", "La CANTIDAD DE TIEMPO en la que quieres que te avise.", true),
                        new OptionData(STRING, "unidad_tiempo", "La UNIDAD DE TIEMPO de la cantidad especificada: s (segundos), m (minutos), h (horas) o d (días).", true),
                        new OptionData(STRING, "nombre", "El nombre del temporizador. Máximo 30 caracteres."));

        SubcommandData consultarTemporizador = new SubcommandData("consultar", "Consulta tus temporizadores activos.");

        SubcommandData eliminarTemporizador = new SubcommandData("eliminar", "Elimina un temporizador.")
                .addOptions(new OptionData(INTEGER, "identificador", "El identificador del temporizador que quieres eliminar. "
                        + "Consíguelo con el subcomando consultar.", true));

        comandos.addCommands(
                new CommandData("temporizador", "Haz que Butterfree te avise cuando pase el tiempo que le digas.")
                        .addSubcommands(crearTemporizador, consultarTemporizador, eliminarTemporizador));

        comandos.addCommands(new CommandData("purgar", "Solo puede usarlo el Equipo Administrativo. "
                + "Elimina mensajes en lote.")
                .addOptions(new OptionData(INTEGER, "cantidad", "La cantidad de mensajes a eliminar.", true)
                        .setRequiredRange(1, 30))
                .addOptions(new OptionData(USER, "usuarie", "Si se especifica, borra mensajes de ese usuarie en concreto.")));

        comandos.addCommands(new CommandData("bienvenide", "Solo puede usarlo el Equipo Administrativo. "
                + "Da la bienvenida a alguien al servidor.")
                .addSubcommands(new SubcommandData("miembre", "Da la bienvenida a une miembre.")
                        .addOptions(new OptionData(USER, "usuarie", "La persona a quien dar la bienvenida.", true))
                        .addOptions(new OptionData(STRING, "pronombre", "Terminación de las palabras. Máx. 4 caracteres. Si no lo especificas, depende de los roles.")))
                .addSubcommands(new SubcommandData("aliade", "Da la bienvenida a une aliade.")
                        .addOptions(new OptionData(USER, "usuarie", "La persona a quien dar la bienvenida.", true))
                        .addOptions(new OptionData(STRING, "pronombre", "Terminación de las palabras. Máx. 4 caracteres. Si no lo especificas, depende de los roles."))));

        comandos.addCommands(new CommandData("fecha-union", "Te indica cuándo te has unido al servidor."));
        comandos.addCommands(new CommandData("fecha-creacion", "Te indica cuándo has creado tu cuenta de Discord."));

        SubcommandData crearAdvertencia = new SubcommandData("crear", "Crea una advertencia. ESTO ENVÍA MD.")
                .addOptions(new OptionData(USER, "usuarie", "¿A quién quieres advertir?", true),
                        new OptionData(STRING, "motivo", "Debes anotar un motivo.", true),
                        new OptionData(INTEGER, "tiempo", "CANTIDAD DE TIEMPO para expirar. Debes poner la unidad en la opción unidad_tiempo."),
                        new OptionData(STRING, "unidad_tiempo", "La UNIDAD DE TIEMPO de la cantidad especificada: h (horas), d (días), m (meses) o a (años)."));

        SubcommandData consultarAdvertencia = new SubcommandData("consultar", "Consulta las advertencias activas.")
                .addOption(USER, "usuarie", "Si especificas la persona a consultar, se limitará la búsqueda y se mostrarán los motivos y tiempo.");

        SubcommandData eliminarAdvertencia = new SubcommandData("eliminar", "Elimina una advertencia. ESTO ENVÍA MD.")
                .addOptions(new OptionData(INTEGER, "identificador", "El identificador de la advertencia que quieres eliminar.", true));

        SubcommandData limpiarAdvertencias = new SubcommandData("limpiar", "Elimina todas las advertencias de alguien. ESTO ENVÍA MD.")
                .addOptions(new OptionData(USER, "usuarie", "Usuarie cuyas advertencias deseas limpiar.", true));

        comandos.addCommands(
                new CommandData("advertencia", "Haz que Butterfree te avise cuando pase el tiempo que le digas")
                        .addSubcommands(crearAdvertencia, consultarAdvertencia, eliminarAdvertencia, limpiarAdvertencias));

        comandos.queue();

        StringBuilder logComandos = new StringBuilder();

        for (Command c : jda.getGuilds().get(0).retrieveCommands().complete()) {
            logComandos.append("- ").append(c.getName()).append("\n");

            for (Command.Subcommand s : c.getSubcommands()) {
                logComandos.append("-- ").append(s.getName()).append("\n");
            }
        }

        log.debug("... Cargados los siguientes comandos:\n" + logComandos);

        log.debug("Cantidad de comandos slash cargados: " + jda.getGuilds().get(0).retrieveCommands().complete().size());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent evento) {
        log.info(evento.getUser().getAsTag() + " (" + evento.getUser().getId() + ") llamó a Slash Command " + evento.getCommandString());

        if (evento.getGuild() == null) {
            return;
        }

        if (evento.getMember().getRoles().isEmpty()) {
            evento.reply("Error: solo las personas admitidas en el servidor pueden utilizar mis comandos.").setEphemeral(true).queue();
            return;
        }

        switch (evento.getName()) {
            case "hola":
                Genericos.hola(evento);
                break;
            case "latencia":
                Sistema.latencia(evento);
                break;
            case "temporizador":
                if (evento.getSubcommandName() != null) {
                    switch (evento.getSubcommandName()) {
                        case "crear" -> Utilidades.crearTemporizador(evento);
                        case "consultar" -> Utilidades.consultarTemporizador(evento);
                        case "eliminar" -> Utilidades.eliminarTemporizador(evento);
                        default -> evento.reply("Ese subcomando no existe.").setEphemeral(true).queue();
                    }
                } else {
                    evento.reply("Debes proporcionar un subcomando (crear, consultar o eliminar).").setEphemeral(true).queue();
                }

                break;
            case "purgar":
                Administrativos.purgar(evento);
                break;
            case "bienvenide":
                if (evento.getSubcommandName() != null) {
                    switch (evento.getSubcommandName()) {
                        case "miembre" -> Administrativos.bienvenideMiembre(evento);
                        case "aliade" -> Administrativos.bienvenideAliade(evento);
                        default -> evento.reply("Ese subcomando no existe.").setEphemeral(true);
                    }
                } else {
                    evento.reply("Debes proporcionar un subcomando (miembre o aliade).").setEphemeral(true).queue();
                }

                break;
            case "fecha-union":
                Utilidades.fechaUnion(evento);
                break;
            case "fecha-creacion":
                Utilidades.fechaCreacion(evento);
                break;
            case "advertencia":
                if (evento.getSubcommandName() != null) {
                    switch (evento.getSubcommandName()) {
                        case "crear" -> Moderacion.crearAdvertencia(evento);
                        case "consultar" -> Moderacion.consultarAdvertencia(evento);
                        case "eliminar" -> Moderacion.eliminarAdvertencia(evento);
                        case "limpiar" -> Moderacion.limpiarAdvertencias(evento);
                        default -> evento.reply("Ese subcomando no existe.").setEphemeral(true);
                    }
                } else {
                    evento.reply("Debes proporcionar un subcomando (crear, consultar o eliminar).").setEphemeral(true).queue();
                }

                break;
            default:
                evento.reply("Ese comando no existe.").setEphemeral(true).queue();
        }
    }
}
