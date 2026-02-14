package com.mars.my_app.inventory;

import com.mars.my_app.util.JsonStringFormatter;
import com.mars.my_app.util.WebUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public InventoryController(final InventoryService inventoryService,
            final ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @InitBinder
    public void jsonFormatting(final WebDataBinder binder) {
        binder.addCustomFormatter(new JsonStringFormatter<List<String>>(objectMapper) {
        }, "condition");
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("inventories", inventoryService.findAll());
        return "inventory/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("inventory") final InventoryDTO inventoryDTO) {
        return "inventory/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("inventory") @Valid final InventoryDTO inventoryDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "inventory/add";
        }
        inventoryService.create(inventoryDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("inventory.create.success"));
        return "redirect:/inventories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("inventory", inventoryService.get(id));
        return "inventory/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("inventory") @Valid final InventoryDTO inventoryDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "inventory/edit";
        }
        inventoryService.update(id, inventoryDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("inventory.update.success"));
        return "redirect:/inventories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        inventoryService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("inventory.delete.success"));
        return "redirect:/inventories";
    }

}
