package launchcmd.launchcmd;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.text.Text;
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
					.then((CommandManager.argument("targets", EntityArgumentType.entities())
							.then(CommandManager.literal("addMotion")
								.then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
												.then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
													.executes((context) -> {
														return launchAddMotion((ServerCommandSource) context.getSource(),
																EntityArgumentType.getEntities(context, "targets"),
																DoubleArgumentType.getDouble(context, "motionX"),
																DoubleArgumentType.getDouble(context, "motionY"),
																DoubleArgumentType.getDouble(context, "motionZ"));
													})
												)
										)
								)
							)
							.then(CommandManager.literal("setMotion")
								.then(CommandManager.argument("motionX", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("motionY", DoubleArgumentType.doubleArg())
												.then(CommandManager.argument("motionZ", DoubleArgumentType.doubleArg())
														.executes((context) -> {
															return launchSetMotion((ServerCommandSource) context.getSource(),
																	EntityArgumentType.getEntities(context, "targets"),
																	DoubleArgumentType.getDouble(context, "motionX"),
																	DoubleArgumentType.getDouble(context, "motionY"),
																	DoubleArgumentType.getDouble(context, "motionZ"));
														})
												)
										)
								)
							)
							.then(CommandManager.literal("looking")
									.then(CommandManager.argument("force", DoubleArgumentType.doubleArg())
										.executes((context) -> {
											return launchLooking((ServerCommandSource) context.getSource(),
													EntityArgumentType.getEntities(context, "targets"),
													DoubleArgumentType.getDouble(context, "force"));
										})
									)
							)
					))
			);
		});
	}

	private static int launchAddMotion(ServerCommandSource source, Collection<? extends Entity> entityToLaunch, double motX, double motY, double motZ) {
		Iterator entitiesCollection = entityToLaunch.iterator();

		while (entitiesCollection.hasNext()) {
			Entity target = (Entity) entitiesCollection.next();
			//source.sendFeedback(() -> Text.of("Add motion to " + ((LivingEntity)target).getName() + "with x:" + motX + " y:" + motY + " z:" + motZ), false);
			target.addVelocity(motX, motY, motZ);
			target.velocityModified = true;
		}

		return 1;
	}

	private static int launchSetMotion(ServerCommandSource source, Collection<? extends Entity> entityToLaunch, double motX, double motY, double motZ) {
		Iterator entitiesCollection = entityToLaunch.iterator();

		while (entitiesCollection.hasNext()) {
			Entity target = (Entity) entitiesCollection.next();
			//source.sendFeedback(() -> Text.of("Set motion to " + ((LivingEntity)target).getName() + "with x:" + motX + " y:" + motY + " z:" + motZ), false);
			target.setVelocity(motX, motY, motZ);
			target.velocityModified = true;
		}

		return 1;
	}

	private static int launchLooking(ServerCommandSource source, Collection<? extends Entity> entityToLaunch, double force) {
		//angle is in degree, we need to convert it to radian
		float sourcePitch = (float)((-(source.getRotation().x) * (Math.PI / 180.0f)) + Math.PI / 2);
		float sourceYaw = (float)((source.getRotation().y * (Math.PI / 180.0f)) + Math.PI / 2);

		Iterator entitiesCollection = entityToLaunch.iterator();

		while (entitiesCollection.hasNext()) {
			Entity target = (Entity) entitiesCollection.next();
			//source.sendFeedback(() -> Text.of("Set motion to " + target.getName() + " with force:" + force + " pitch(x):" + sourceYaw + " yaw(y):" + sourcePitch), false);
			target.addVelocity(
					force * (Math.cos(sourceYaw)*Math.sin(sourcePitch)),
					force * Math.sin(sourcePitch - Math.PI / 2),
					force * (Math.sin(sourceYaw)*Math.sin(sourcePitch))
			);
			target.velocityModified = true;
		}

		return 1;
	}
}