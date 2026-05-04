package com.knopp.antimending.listener;

import com.knopp.antimending.AntiMendingPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MendingListener implements Listener {

    private final AntiMendingPlugin plugin;
    private Enchantment mendingEnchant;

    public MendingListener(AntiMendingPlugin plugin) {
        this.plugin = plugin;
        try {
            // Métodos modernos de registro en Paper 1.21
            this.mendingEnchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("mending"));
        } catch (Exception e) {
            plugin.getLogger().warning("No se pudo cargar el encantamiento Mending del registro, usando fallback.");
        }
    }

    private boolean stripMending(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        if (!item.hasItemMeta()) return false;
        if (mendingEnchant == null) return false;

        boolean stripped = false;
        ItemMeta meta = item.getItemMeta();

        if (meta.hasEnchant(mendingEnchant)) {
            meta.removeEnchant(mendingEnchant);
            stripped = true;
        }

        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            if (storageMeta.hasStoredEnchant(mendingEnchant)) {
                storageMeta.removeStoredEnchant(mendingEnchant);
                stripped = true;
            }
        }

        if (stripped) {
            item.setItemMeta(meta);
        }

        return stripped;
    }

    private boolean hasMending(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        if (!item.hasItemMeta()) return false;
        if (mendingEnchant == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta.hasEnchant(mendingEnchant)) return true;
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            return storageMeta.hasStoredEnchant(mendingEnchant);
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerItemMend(PlayerItemMendEvent event) {
        // Bloquea el evento para que la experiencia no repare el objeto
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null && stripMending(result)) {
            event.setResult(result);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLootGenerate(LootGenerateEvent event) {
        List<ItemStack> items = event.getLoot();
        for (ItemStack item : items) {
            stripMending(item);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        MerchantRecipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();
        
        if (hasMending(result)) {
            // Cancelar completamente el evento hace que el aldeano no obtenga este trade en absoluto
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item itemEntity) {
            ItemStack item = itemEntity.getItemStack();
            if (stripMending(item)) {
                itemEntity.setItemStack(item);
            }
        }
    }
}
