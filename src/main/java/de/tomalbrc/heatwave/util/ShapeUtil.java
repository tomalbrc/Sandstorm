package de.tomalbrc.heatwave.util;

import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.polymer.ParticleEffectHolder;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.world.phys.Vec3;

public class ShapeUtil {
    public static ParticleEffectHolder.InitialParticleData initialParticleData(MolangRuntime runtime, ParticleEffectHolder holder) throws MolangRuntimeException {
        var point = holder.get(ParticleComponents.EMITTER_SHAPE_POINT);
        if (point != null) {
            var x = runtime.resolve(point.offset[0]);
            var y = runtime.resolve(point.offset[1]);
            var z = runtime.resolve(point.offset[2]);
            var dx = runtime.resolve(point.direction[0]);
            var dy = runtime.resolve(point.direction[1]);
            var dz = runtime.resolve(point.direction[2]);
            return ParticleEffectHolder.InitialParticleData.of(new Vec3(x, y, z), new Vec3(dx, dy, dz));
        }

        var sphere = holder.get(ParticleComponents.EMITTER_SHAPE_SPHERE);
        if (sphere != null) {
            var rad = runtime.resolve(sphere.radius);
            var x = runtime.resolve(sphere.offset[0]);
            var y = runtime.resolve(sphere.offset[1]);
            var z = runtime.resolve(sphere.offset[2]);

            float theta = (float) (Math.random() * 2 * Math.PI);
            float phi = (float) Math.acos(2 * Math.random() - 1);
            float dx = (float) (Math.sin(phi) * Math.cos(theta));
            float dy = (float) Math.cos(phi);
            float dz = (float) (Math.sin(phi) * Math.sin(theta));

            var v = new Vec3(x, y, z).add(dx * (rad * (sphere.surfaceOnly ? 1 : (float) Math.random())), dy * (rad * (sphere.surfaceOnly ? 1 : (float) Math.random())), dz * (rad * (sphere.surfaceOnly ? 1 : (float) Math.random())));
            var n = v.normalize();

            if (sphere.directionList != null) {
                var dlx = runtime.resolve(sphere.directionList.get(0));
                var dly = runtime.resolve(sphere.directionList.get(1));
                var dlz = runtime.resolve(sphere.directionList.get(2));
                return ParticleEffectHolder.InitialParticleData.of(v, new Vec3(dlx, dly, dlz));
            } else if (sphere.direction == EmitterDirection.INWARDS) {
                n = n.reverse();
            }

            return ParticleEffectHolder.InitialParticleData.of(v, n);
        }

        var box = holder.get(ParticleComponents.EMITTER_SHAPE_BOX);
        if (box != null) {
            var w = runtime.resolve(box.halfDimensions[0]);
            var h = runtime.resolve(box.halfDimensions[1]);
            var b = runtime.resolve(box.halfDimensions[2]);
            var x = runtime.resolve(box.offset[0]);
            var y = runtime.resolve(box.offset[1]);
            var z = runtime.resolve(box.offset[2]);

            float px = 0, py = 0, pz = 0;
            if (box.surfaceOnly) {
                int face = (int) (Math.random() * 6);
                switch (face) {
                    case 0 -> {
                        px = w;
                        py = (float) (Math.random() * 2 - 1) * h;
                        pz = (float) (Math.random() * 2 - 1) * b;
                    }
                    case 1 -> {
                        px = -w;
                        py = (float) (Math.random() * 2 - 1) * h;
                        pz = (float) (Math.random() * 2 - 1) * b;
                    }
                    case 2 -> {
                        px = (float) (Math.random() * 2 - 1) * w;
                        py = h;
                        pz = (float) (Math.random() * 2 - 1) * b;
                    }
                    case 3 -> {
                        px = (float) (Math.random() * 2 - 1) * w;
                        py = -h;
                        pz = (float) (Math.random() * 2 - 1) * b;
                    }
                    case 4 -> {
                        px = (float) (Math.random() * 2 - 1) * w;
                        py = (float) (Math.random() * 2 - 1) * h;
                        pz = b;
                    }
                    case 5 -> {
                        px = (float) (Math.random() * 2 - 1) * w;
                        py = (float) (Math.random() * 2 - 1) * h;
                        pz = -b;
                    }
                }
            } else {
                px = (float) (Math.random() * 2 - 1) * w;
                py = (float) (Math.random() * 2 - 1) * h;
                pz = (float) (Math.random() * 2 - 1) * b;
            }

            var v = new Vec3(x + px, y + py, z + pz);
            var n = v.normalize();
            if (box.directionList != null) {
                var dlx = runtime.resolve(box.directionList.get(0));
                var dly = runtime.resolve(box.directionList.get(1));
                var dlz = runtime.resolve(box.directionList.get(2));
                return ParticleEffectHolder.InitialParticleData.of(v, new Vec3(dlx, dly, dlz));
            } else if (box.direction == EmitterDirection.INWARDS) {
                n = n.reverse();
            }

            return ParticleEffectHolder.InitialParticleData.of(v, n);
        }

        var disc = holder.get(ParticleComponents.EMITTER_SHAPE_DISC);
        if (disc != null) {
            var r = runtime.resolve(disc.radius);
            var nx = runtime.resolve(disc.planeNormal[0]);
            var ny = runtime.resolve(disc.planeNormal[1]);
            var nz = runtime.resolve(disc.planeNormal[2]);
            var x = runtime.resolve(disc.offset[0]);
            var y = runtime.resolve(disc.offset[1]);
            var z = runtime.resolve(disc.offset[2]);

            float angle = (float) (Math.random() * 2 * Math.PI);
            float radius = disc.surfaceOnly ? r : (float) Math.random() * r;

            float dx = (float) (Math.cos(angle) * radius);
            float dy = (float) (Math.sin(angle) * radius);

            // orthogonal vector
            float ux = ny * nz - nx * nz; // cross prod
            float uy = -nx * nz;
            float uz = nx * ny;

            var v = new Vec3(x, y, z).add(nx * dx + ux * dy, ny * dx + uy * dy, nz * dx + uz * dy);
            var n = v.normalize();
            if (disc.directionList != null) {
                var dlx = runtime.resolve(disc.directionList[0]);
                var dly = runtime.resolve(disc.directionList[1]);
                var dlz = runtime.resolve(disc.directionList[2]);
                return ParticleEffectHolder.InitialParticleData.of(v, new Vec3(dlx, dly, dlz));
            } else if (disc.direction == EmitterDirection.INWARDS) {
                n = n.reverse();
            }

            return ParticleEffectHolder.InitialParticleData.of(v, n);
        }

        var custom = holder.get(ParticleComponents.EMITTER_SHAPE_CUSTOM);
        if (custom != null) {
            var dx = runtime.resolve(custom.direction[0]);
            var dy = runtime.resolve(custom.direction[1]);
            var dz = runtime.resolve(custom.direction[2]);
            var x = runtime.resolve(custom.offset[0]);
            var y = runtime.resolve(custom.offset[1]);
            var z = runtime.resolve(custom.offset[2]);
            return ParticleEffectHolder.InitialParticleData.of(new Vec3(x, y, z), new Vec3(dx, dy, dz));
        }

        return ParticleEffectHolder.InitialParticleData.ZERO;
    }
}
