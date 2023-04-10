package josscoder.easteregghunt.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.factory.EasterEggFactory;

public class RegisterBunnyCommand extends Command {

    private final EasterEggFactory easterEggFactory;

    public RegisterBunnyCommand() {
        super("registerbunny", "Register Easter Bunny");
        setPermission("register.bunny.command.permission");
        easterEggFactory = EasterEggFactory.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !testPermission(sender)) {
            return false;
        }

        Player player = (Player) sender;

        if (easterEggFactory.isBunnyRegistered()) {
            player.sendMessage(TextFormat.RED + "The position of the rabbit is already registered, delete it from the configuration and restart the server");
            return false;
        }

        Position position = player.getPosition();
        easterEggFactory.setBunny(position, true);

        player.sendMessage(TextFormat.colorize(String.format("&bEaster Bunny &bwas registered successfully in &6(X: %s, Y: %s, Z: %s, World: %s)&b!",
                (int) position.getX(),
                (int) position.getY(),
                (int) position.getZ(),
                position.getLevel().getFolderName()
        )));

        return true;
    }
}
