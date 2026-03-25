package com.ecomm.nrt.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDTO {
	
	private Long id;
	private String menuName;
	private String icon;
	private Integer sequence;
	private String pageName;
	private String roleCode;
	private Long parentId;
	private List<MenuDTO> subMenus;
	
}
