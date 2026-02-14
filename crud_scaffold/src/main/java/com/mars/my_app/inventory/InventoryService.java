package com.mars.my_app.inventory;

import com.mars.my_app.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(final InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryDTO> findAll() {
        final List<Inventory> inventories = inventoryRepository.findAll(Sort.by("id"));
        return inventories.stream()
                .map(inventory -> mapToDTO(inventory, new InventoryDTO()))
                .toList();
    }

    public InventoryDTO get(final Long id) {
        return inventoryRepository.findById(id)
                .map(inventory -> mapToDTO(inventory, new InventoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final InventoryDTO inventoryDTO) {
        final Inventory inventory = new Inventory();
        mapToEntity(inventoryDTO, inventory);
        return inventoryRepository.save(inventory).getId();
    }

    public void update(final Long id, final InventoryDTO inventoryDTO) {
        final Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(inventoryDTO, inventory);
        inventoryRepository.save(inventory);
    }

    public void delete(final Long id) {
        final Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        inventoryRepository.delete(inventory);
    }

    private InventoryDTO mapToDTO(final Inventory inventory, final InventoryDTO inventoryDTO) {
        inventoryDTO.setId(inventory.getId());
        inventoryDTO.setSpecies(inventory.getSpecies());
        inventoryDTO.setLengthInInches(inventory.getLengthInInches());
        inventoryDTO.setWidthInInches(inventory.getWidthInInches());
        inventoryDTO.setThicknesInInches(inventory.getThicknesInInches());
        inventoryDTO.setCondition(inventory.getCondition());
        inventoryDTO.setType(inventory.getType());
        inventoryDTO.setLiveEdge(inventory.getLiveEdge());
        return inventoryDTO;
    }

    private Inventory mapToEntity(final InventoryDTO inventoryDTO, final Inventory inventory) {
        inventory.setSpecies(inventoryDTO.getSpecies());
        inventory.setLengthInInches(inventoryDTO.getLengthInInches());
        inventory.setWidthInInches(inventoryDTO.getWidthInInches());
        inventory.setThicknesInInches(inventoryDTO.getThicknesInInches());
        inventory.setCondition(inventoryDTO.getCondition());
        inventory.setType(inventoryDTO.getType());
        inventory.setLiveEdge(inventoryDTO.getLiveEdge());
        return inventory;
    }

}
