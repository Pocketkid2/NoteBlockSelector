package com.github.pocketkid2.noteblockselector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NoteBlockListener implements Listener {

	private NoteBlockSelectorPlugin plugin;

	private static final String name = "Choose your note:";

	private List<Note> noteList;

	private Map<Player, Block> openPlayers;

	public NoteBlockListener(NoteBlockSelectorPlugin p) {
		plugin = p;

		Set<Note> notes = new HashSet<>();
		for (Note.Tone tone : Note.Tone.values()) {
			notes.add(new Note(0, tone, false));
			notes.add(new Note(0, tone, true));
			notes.add(new Note(1, tone, false));
			notes.add(new Note(1, tone, true));
			if (tone == Note.Tone.F) {
				notes.add(new Note(2, tone, true));
			}
		}
		noteList = notes.stream().collect(Collectors.toList());

		openPlayers = new HashMap<>();

		plugin.getLogger().info("Notes list has " + notes.size() + " items!");
	}

	@EventHandler
	public void onPlayerClickBlock(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
			event.setCancelled(true);

			Inventory inv = Bukkit.createInventory(event.getPlayer(), InventoryType.CHEST, name);

			ItemStack[] contents = inv.getContents();

			for (int i = 0; i < noteList.size(); i++) {
				Material woolColor = null;
				switch (noteList.get(i).getTone()) {
				case A:
					woolColor = Material.RED_WOOL;
					break;
				case B:
					woolColor = Material.ORANGE_WOOL;
					break;
				case C:
					woolColor = Material.YELLOW_WOOL;
					break;
				case D:
					woolColor = Material.LIME_WOOL;
					break;
				case E:
					woolColor = Material.LIGHT_BLUE_WOOL;
					break;
				case F:
					woolColor = Material.BLUE_WOOL;
					break;
				case G:
					woolColor = Material.PURPLE_WOOL;
					break;
				default:
					break;

				}
				contents[i] = new ItemStack(woolColor);
				ItemMeta meta = contents[i].getItemMeta();
				meta.setDisplayName(String.format("Note: %s, Octave: %d",
						noteList.get(i).getTone().name() + (noteList.get(i).isSharped() ? "#" : ""),
						noteList.get(i).getOctave()));
				contents[i].setItemMeta(meta);
			}

			inv.setContents(contents);

			event.getPlayer().openInventory(inv);
			openPlayers.put(event.getPlayer(), event.getClickedBlock());
		}
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player && name.equalsIgnoreCase(event.getView().getTitle())) {
			event.setCancelled(true);
			plugin.getLogger().info("Found click: Inventory Slot: " + event.getSlot() + " with item "
					+ event.getCurrentItem().toString());
			NoteBlock nb = (NoteBlock) openPlayers.get(event.getWhoClicked()).getBlockData();
			nb.setNote(noteList.get(event.getSlot()));
			openPlayers.get(event.getWhoClicked()).setBlockData(nb);
		}
	}
}
