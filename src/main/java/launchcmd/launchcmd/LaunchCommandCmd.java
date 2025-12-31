package launchcmd.launchcmd;

import net.fabricmc.api.ModInitializer;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class LaunchCommandCmd implements ModInitializer {
	public static final String MOD_ID = "launch-command";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("launch")
					.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
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
									.then(
											Commands.literal("setMotionX")
													.then(
															Commands.argument("motionX", DoubleArgumentType.doubleArg())
																	.executes(context -> launchSetMotionX(
																			context.getSource(),
																			EntityArgument.getEntities(context, "targets"),
																			DoubleArgumentType.getDouble(context, "motionX")
																	))
													)
									)
									.then(
											Commands.literal("setMotionY")
													.then(
															Commands.argument("motionY", DoubleArgumentType.doubleArg())
																	.executes(context -> launchSetMotionY(
																			context.getSource(),
																			EntityArgument.getEntities(context, "targets"),
																			DoubleArgumentType.getDouble(context, "motionY")
																	))
													)
									)
									.then(
											Commands.literal("setMotionZ")
													.then(
															Commands.argument("motionZ", DoubleArgumentType.doubleArg())
																	.executes(context -> launchSetMotionZ(
																			context.getSource(),
																			EntityArgument.getEntities(context, "targets"),
																			DoubleArgumentType.getDouble(context, "motionZ")
																	))
													)
									)
					)
			);
		});
	}


	private static int launchAddMotion(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double motX, double motY, double motZ) {
		for (Entity entity : entitiesToLaunch) {
			Vec3 motion = new Vec3(motX, motY, motZ);
			entity.addDeltaMovement(motion);

			//fix player not being affected
			entity.hurtMarked = true;
			entity.needsSync = true;

			if (entity instanceof Player player) {
				//I have no idea what this does but it was in the ApplyEntityImpulse class
				player.applyPostImpulseGraceTime(10);
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
			entity.hurtMarked = true;
			entity.needsSync = true;

			if (entity instanceof Player player) {
				//I have no idea what this does but it was in the ApplyEntityImpulse class
				player.applyPostImpulseGraceTime(10);
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
			entity.hurtMarked = true;
			entity.needsSync = true;

			if (entity instanceof Player player) {
				//I have no idea what this does but it was in the ApplyEntityImpulse class
				player.applyPostImpulseGraceTime(10);
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

	private static int launchSetMotionX(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double motX) {
		for (Entity entity : entitiesToLaunch) {
			entity.setDeltaMovement(motX, entity.getDeltaMovement().y(), entity.getDeltaMovement().z());

			//fix player not being affected
			entity.hurtMarked = true;
			entity.needsSync = true;

			if (entity instanceof Player player) {
				//I have no idea what this does but it was in the ApplyEntityImpulse class
				player.applyPostImpulseGraceTime(10);
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

	private static int launchSetMotionY(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double motY) {
		for (Entity entity : entitiesToLaunch) {
			entity.setDeltaMovement(entity.getDeltaMovement().x(), motY, entity.getDeltaMovement().z());

			//fix player not being affected
			entity.hurtMarked = true;
			entity.needsSync = true;

			if (entity instanceof Player player) {
				//I have no idea what this does but it was in the ApplyEntityImpulse class
				player.applyPostImpulseGraceTime(10);
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

	private static int launchSetMotionZ(CommandSourceStack source, Collection<? extends Entity> entitiesToLaunch, double motZ) {
		for (Entity entity : entitiesToLaunch) {
			entity.setDeltaMovement(entity.getDeltaMovement().x(), entity.getDeltaMovement().y(), motZ);

			//fix player not being affected
			entity.hurtMarked = true;
			entity.needsSync = true;

			if (entity instanceof Player player) {
				//I have no idea what this does but it was in the ApplyEntityImpulse class
				player.applyPostImpulseGraceTime(10);
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
}