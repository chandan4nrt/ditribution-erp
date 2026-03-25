package com.ecomm.nrt.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecomm.nrt.dto.MenuDTO;
import com.ecomm.nrt.entity.Menu;
import com.ecomm.nrt.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all top-level menus with their children.
     */
    public List<MenuDTO> getAllMenus() {
        return menuRepository.findByParentIsNullOrderBySequenceAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get top-level menus filtered by roleCode with their children.
     */
    public List<MenuDTO> getMenusByRoleCode(String roleCode) {
        return menuRepository.findByRoleCodeAndParentIsNullOrderBySequenceAsc(roleCode).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Add a newly created menu item.
     */
    @Transactional
    @SuppressWarnings("null")
    public MenuDTO addMenu(MenuDTO menuDto) {
        Menu menu = convertToEntity(menuDto);
        java.util.Objects.requireNonNull(menu, "Menu cannot be null");
        Menu savedMenu = menuRepository.save(menu);
        return convertToDto(savedMenu);
    }

    /**
     * Add a list of menu items.
     */
    @Transactional
    @SuppressWarnings("null")
    public List<MenuDTO> addMenus(List<MenuDTO> menuDtoList) {
        if (menuDtoList == null) return java.util.Collections.emptyList();
        List<Menu> menus = menuDtoList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        java.util.Objects.requireNonNull(menus, "Menus list cannot be null");
        List<Menu> savedMenus = menuRepository.saveAll(menus);
        return savedMenus.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Add a sub-menu to a specific parent.
     */
    @Transactional
    @SuppressWarnings("null")
    public MenuDTO addSubMenu(Long parentId, MenuDTO subMenuDto) {
        java.util.Objects.requireNonNull(parentId, "Parent ID cannot be null");
        Menu parent = menuRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent menu not found with ID: " + parentId));
        Menu subMenu = convertToEntity(subMenuDto);
        java.util.Objects.requireNonNull(subMenu, "Sub-menu cannot be null");
        subMenu.setParent(parent);
        Menu savedSubMenu = menuRepository.save(subMenu);
        return convertToDto(savedSubMenu);
    }

    private MenuDTO convertToDto(Menu menu) {
        MenuDTO dto = modelMapper.map(menu, MenuDTO.class);
        if (menu.getParent() != null) {
            Long parentId = menu.getParent().getId();
            java.util.Objects.requireNonNull(parentId, "Existing parent ID cannot be null");
            dto.setParentId(parentId);
        }
        if (menu.getSubMenus() != null && !menu.getSubMenus().isEmpty()) {
            dto.setSubMenus(menu.getSubMenus().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    @SuppressWarnings("null")
    private Menu convertToEntity(MenuDTO dto) {
        Menu menu = modelMapper.map(dto, Menu.class);
        
        // Handle explicit parentId if provided and not nested
        if (dto.getParentId() != null && menu.getParent() == null) {
            menuRepository.findById(dto.getParentId()).ifPresent(menu::setParent);
        }

        if (dto.getSubMenus() != null && !dto.getSubMenus().isEmpty()) {
            List<Menu> subMenus = dto.getSubMenus().stream()
                    .map(subDto -> {
                        Menu subMenu = convertToEntity(subDto);
                        subMenu.setParent(menu);
                        return subMenu;
                    })
                    .collect(Collectors.toList());
            menu.setSubMenus(subMenus);
        }
        return menu;
    }
}
