package josscoder.easteregghunt.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.egg.factory.EasterEggFactory;
import josscoder.easteregghunt.egg.factory.data.EasterEgg;

public class RegisterEggCommand extends Command {

    private final EasterEggFactory easterEggFactory;

    public RegisterEggCommand() {
        super("registeregg", "Register a EasterEgg");
        setPermission("register.egg.command.permission");
        easterEggFactory = EasterEggFactory.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !testPermission(sender)) {
            return false;
        }

        Player player = (Player) sender;

        if (easterEggFactory.getEasterEggs().size() >= EasterEggFactory.MAX_EGGS) {
            player.sendMessage(TextFormat.RED + String.format("Only %s easter eggs can be registered!", EasterEggFactory.MAX_EGGS));
            return false;
        }

        EasterEgg easterEgg = easterEggFactory.registerAngGetEgg(player.getPosition());
        Position position = easterEgg.getPosition();

        player.sendMessage(TextFormat.colorize(String.format("&bEasterEgg &6#%s &bwas registered successfully in &6(X: %s, Y: %s, Z: %s, World: %s)&b!",
                easterEgg.getId(),
                (int) position.getX(),
                (int) position.getY(),
                (int) position.getZ(),
                position.getLevel().getFolderName()
        )));

        return true;
    }
}
