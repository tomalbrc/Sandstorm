package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

// Initial State Components
public class EmitterInitialization implements ParticleComponent<EmitterInitialization> {
    @SerializedName("creation_expression")
    public MolangExpression creationExpression; // run once at startup

    @SerializedName("per_update_expression")
    public MolangExpression perUpdateExpression; // run once per update
}
