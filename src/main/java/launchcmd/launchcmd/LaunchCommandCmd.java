package launchcmd.launchcmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class LaunchCommandCmd {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register (
                Commands.literal("launch")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.entities())
                                        .then(
                                                Commands.literal("addMotion")
                                                        .then(
                                                                Commands.argument("motionX", DoubleArgumentType.doubleArg())
                                                                        .then(
                                                                                Commands.argument("motionY", DoubleArgumentType.doubleArg())
                                                                                        .then(
                                                                                                Commands.argument("motionZ", DoubleArgumentType.doubleArg())
                                                                                                        .executes(context -> launchAddMotion(
                                                                                                                context.getSource(),
                                                                                                                EntityArgument.getEntities(context, "targets"),
                                                                                                                DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                                DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                                DoubleArgumentType.getDouble(context, "motionZ"))

                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .then(
                                                Commands.literal("setMotion")
                                                        .then(
                                                                Commands.argument("motionX", DoubleArgumentType.doubleArg())
                                                                        .then(
                                                                                Commands.argument("motionY", DoubleArgumentType.doubleArg())
                                                                                        .then(
                                                                                                Commands.argument("motionZ", DoubleArgumentType.doubleArg())
                                                                                                        .executes(context -> launchSetMotion(
                                                                                                                context.getSource(),
                                                                                                                EntityArgument.getEntities(context, "targets"),
                                                                                                                DoubleArgumentType.getDouble(context, "motionX"),
                                                                                                                DoubleArgumentType.getDouble(context, "motionY"),
                                                                                                                DoubleArgumentType.getDouble(context, "motionZ"))

                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .then(
                                                Commands.literal("looking")
                                                        .then(
                                                                Commands.argument("force", DoubleArgumentType.doubleArg())
                                                                        .executes(context -> launchLooking(
                                                                                context.getSource(),
                                                                                EntityArgument.getEntities(context, "targets"),
                                                                                DoubleArgumentType.getDouble(context, "force")
                                                                        ))
                                                        )

                                        )
                        )
        );
    }



    private static int launchAddMotion(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double motX, double motY, double motZ) {
        for (Entity entity : entitiesToLaunch) {
            Vec3 motion = new Vec3(motX, motY, motZ);
            entity.addDeltaMovement(motion);
            //fix player not being affected
            if (entity instanceof Player) {
                entity.hurtMarked = true;
            }
        }

        if(entitiesToLaunch.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.launchcmd.launchadd.success.single", entitiesToLaunch.iterator().next().getDisplayName()), true);
        }
        else {
            source.sendSuccess(() -> Component.translatable("commands.launchcmd.launchadd.success.multiple", entitiesToLaunch.size()), true);
        }

        return entitiesToLaunch.size();
    }
    private static int launchSetMotion(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double motX, double motY, double motZ) {
        for (Entity entity : entitiesToLaunch) {
            entity.setDeltaMovement(motX, motY, motZ);
            //fix player not being affected
            if (entity instanceof Player) {
                entity.hurtMarked = true;
            }
        }

        if(entitiesToLaunch.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.launchcmd.launchset.success.single", entitiesToLaunch.iterator().next().getDisplayName()), true);
        }
        else {
            source.sendSuccess(() -> Component.translatable("commands.launchcmd.launchset.success.multiple", entitiesToLaunch.size()), true);
        }

        return entitiesToLaunch.size();
    }

    private static int launchLooking(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double force) {
        //angle is in degree, we need to convert it to radian
        float sourcePitch = (float)((-(source.getRotation().x) * (Math.PI / 180.0f)) + Math.PI / 2);
        float sourceYaw = (float)((source.getRotation().y * (Math.PI / 180.0f)) + Math.PI / 2);

        for (Entity entity : entitiesToLaunch) {
            Vec3 motion = new Vec3(
                    force * (Math.cos(sourceYaw)*Math.sin(sourcePitch)),
                    force * Math.sin(sourcePitch - Math.PI / 2),
                    force * (Math.sin(sourceYaw)*Math.sin(sourcePitch)));
            entity.addDeltaMovement(motion);
            //fix player not being affected
            if (entity instanceof Player) {
                entity.hurtMarked = true;
            }
        }

        if(entitiesToLaunch.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.launchcmd.launchadd.success.single", entitiesToLaunch.iterator().next().getDisplayName()), true);
        }
        else {
            source.sendSuccess(() -> Component.translatable("commands.launchcmd.launchadd.success.multiple", entitiesToLaunch.size()), true);
        }

        return entitiesToLaunch.size();
    }
}
