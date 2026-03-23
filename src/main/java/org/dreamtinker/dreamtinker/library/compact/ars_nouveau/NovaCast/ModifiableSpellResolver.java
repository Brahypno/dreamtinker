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
    public void resume(Level world) {
        LivingEntity shooter = spellContext.getUnwrappedCaster();
        SpellResolveEvent.Pre spellResolveEvent = new SpellResolveEvent.Pre(world, shooter, this.hitResult, spell, spellContext, this);
        MinecraftForge.EVENT_BUS.post(spellResolveEvent);
        if (spellResolveEvent.isCanceled())
            return;
        while (spellContext.hasNextPart()) {
            AbstractSpellPart part = spellContext.nextPart();
            if (part == null)
                break;
            if (part instanceof AbstractAugment || !part.isEnabled())
                continue;
            SpellStats.Builder builder = new SpellStats.Builder();
            List<AbstractAugment> augments = spell.getAugments(spellContext.getCurrentIndex() - 1, shooter);
            Set<Class<?>> seenTinker = new HashSet<>();
            augments.removeIf(a -> (a instanceof AugmentTinker) && !seenTinker.add(a.getClass()));
            SpellStats stats = builder
                    .setAugments(augments)
                    .addItemsFromEntity(shooter)
                    .build(part, this.hitResult, world, shooter, spellContext);
            if (!(part instanceof AbstractEffect effect))
                continue;

            EffectResolveEvent.Pre preEvent = new EffectResolveEvent.Pre(world, shooter, this.hitResult, spell, spellContext, effect, stats, this);
            if (MinecraftForge.EVENT_BUS.post(preEvent))
                continue;
            effect.onResolve(this.hitResult, world, shooter, stats, spellContext, this);
            MinecraftForge.EVENT_BUS.post(new EffectResolveEvent.Post(world, shooter, this.hitResult, spell, spellContext, effect, stats, this));
        }

        MinecraftForge.EVENT_BUS.post(new SpellResolveEvent.Post(world, shooter, this.hitResult, spell, spellContext, this));
    }

    public SpellResolver getNewResolver(SpellContext context) {
        ModifiableSpellResolver newResolver = new ModifiableSpellResolver(context);
        newResolver.previousResolver = this;
        return newResolver;
    }
}
