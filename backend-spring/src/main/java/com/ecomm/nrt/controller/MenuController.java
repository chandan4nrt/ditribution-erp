package com.ecomm.nrt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomm.nrt.dto.MenuDTO;
import com.ecomm.nrt.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Get menus filtered by roleCode.
     */
    @GetMapping("/v1/menumaster/{roleCode}")
    public ResponseEntity<List<MenuDTO>> getMenusByRole(@org.springframework.web.bind.annotation.PathVariable String roleCode) {
        return ResponseEntity.ok(menuService.getMenusByRoleCode(roleCode));
    }

    /**
     * Get all top-level menus with their children.
     */
    @GetMapping("/menus")
    public ResponseEntity<List<MenuDTO>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    /**
     * Add a single menu item (could have sub-menus nested).
     */
    @PostMapping("/menus")
    public ResponseEntity<MenuDTO> addMenu(@RequestBody MenuDTO menuDto) {
        return ResponseEntity.ok(menuService.addMenu(menuDto));
    }

    /**
     * Add multiple menu items at once.
     */
    @PostMapping("/menus/batch")
    public ResponseEntity<List<MenuDTO>> addMenus(@RequestBody List<MenuDTO> menuDtoList) {
        return ResponseEntity.ok(menuService.addMenus(menuDtoList));
    }

    /**
     * Add a sub-menu to a specific parent (by parent ID).
     */
    @PostMapping("/menus/{parentId}/submenus")
    public ResponseEntity<MenuDTO> addSubMenu(
            @org.springframework.web.bind.annotation.PathVariable Long parentId, 
            @RequestBody MenuDTO menuDto) {
        return ResponseEntity.ok(menuService.addSubMenu(parentId, menuDto));
    }
}
