package launchcmd.launchcmd;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;

public class LaunchCommand implements ModInitializer {
	public static final String MOD_ID = "launch-command";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("launch").requires((source) -> {
				return source.hasPermissionLevel(2);})
					.then((CommandManager.argument("target", EntityArgumentType.entities()).then(CommandManager.literal("addMotion")
						.then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
								.then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
											.executes((context) -> {
												return launchAddMotion((ServerCommandSource) context.getSource(),
														EntityArgumentType.getEntities(context, "target"),
														DoubleArgumentType.getDouble(context, "motionX"),
														DoubleArgumentType.getDouble(context, "motionY"),
														DoubleArgumentType.getDouble(context, "motionZ"));
											}))
								))
					)))
			);
		});
	}

	private static int launchAddMotion(ServerCommandSource source, Collection<? extends Entity> entityToLaunch, double motX, double motY, double motZ) {
		Iterator entitiesCollection = entityToLaunch.iterator();

		while (entitiesCollection.hasNext()) {
			Entity target = (Entity) entitiesCollection.next();
			if(target instanceof LivingEntity) {
				//source.sendFeedback(() -> Text.of("Called entity " + ((LivingEntity)target).getUuidAsString() + "x:" + motX + " y:" + motY + " z:" + motZ), false);
				((LivingEntity)target).addVelocity(motX, motY, motZ);
				((LivingEntity)target).velocityModified = true;
			}
		}

		return 1;
	}
}