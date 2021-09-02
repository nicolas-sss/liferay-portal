/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.headless.admin.taxonomy.internal.resource.v1_0;

import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryPropertyResource;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author José Abelenda
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/taxonomy-category-property.properties",
	scope = ServiceScope.PROTOTYPE,
	service = TaxonomyCategoryPropertyResource.class
)
public class TaxonomyCategoryPropertyResourceImpl
	extends BaseTaxonomyCategoryPropertyResourceImpl {

	@Override
	public Page<TaxonomyCategoryProperty> getTaxonomyCategoryPropertiesPage(
			String taxonomyCategoryId)
		throws Exception {

		TaxonomyCategoryProperty taxonomyCategoryProperty =
			new TaxonomyCategoryProperty();

		taxonomyCategoryProperty.setName("NAICS");
		taxonomyCategoryProperty.setValue("1050");

		return Page.of(Collections.singletonList(taxonomyCategoryProperty));
	}

	@Reference
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

}