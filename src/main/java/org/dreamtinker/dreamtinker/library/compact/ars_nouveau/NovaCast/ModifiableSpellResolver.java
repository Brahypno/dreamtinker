package org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast;

import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifiableSpellResolver extends SpellResolver {
    public ModifiableSpellResolver(SpellContext spellContext) {
        super(spellContext);
    }

    @Override
    protected void resolveAllEffects(Level world) {
        this.spellContext.resetCastCounter();
        LivingEntity shooter = this.spellContext.getUnwrappedCaster();
        SpellResolveEvent.Pre spellResolveEvent = new SpellResolveEvent.Pre(world, shooter, this.hitResult, this.spell, this.spellContext, this);
        MinecraftForge.EVENT_BUS.post(spellResolveEvent);
        if (!spellResolveEvent.isCanceled()){
            while (this.spellContext.hasNextPart()) {
                AbstractSpellPart part = this.spellContext.nextPart();
                if (part == null){
                    break;
                }

                if (!(part instanceof AbstractAugment) && part.isEnabled()){
                    SpellStats.Builder builder = new SpellStats.Builder();
                    List<AbstractAugment> augments = this.spell.getAugments(this.spellContext.getCurrentIndex() - 1, shooter);
                    Set<Class<?>> seenTinker = new HashSet<>();
                    augments.removeIf(a -> (a instanceof AugmentTinker) && !seenTinker.add(a.getClass()));
                    SpellStats stats = builder.setAugments(augments).addItemsFromEntity(shooter).build(part, this.hitResult, world, shooter, this.spellContext);
                    if (part instanceof AbstractEffect effect){
                        EffectResolveEvent.Pre preEvent =
                                new EffectResolveEvent.Pre(world, shooter, this.hitResult, this.spell, this.spellContext, effect, stats, this);
                        if (!MinecraftForge.EVENT_BUS.post(preEvent)){
                            effect.onResolve(this.hitResult, world, shooter, stats, this.spellContext, this);
                            MinecraftForge.EVENT_BUS.post(
                                    new EffectResolveEvent.Post(world, shooter, this.hitResult, this.spell, this.spellContext, effect, stats, this));
                        }
                    }
                }
            }

            MinecraftForge.EVENT_BUS.post(new SpellResolveEvent.Post(world, shooter, this.hitResult, this.spell, this.spellContext, this));
        }
    }

    public SpellResolver getNewResolver(SpellContext context) {
        ModifiableSpellResolver newResolver = new ModifiableSpellResolver(context);
        newResolver.previousResolver = this;
        return newResolver;
    }
}
