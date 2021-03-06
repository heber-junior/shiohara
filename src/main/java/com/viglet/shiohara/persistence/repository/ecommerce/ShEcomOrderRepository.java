/*
 * Copyright (C) 2016-2018 Alexandre Oliveira <alexandre.oliveira@viglet.com> 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.viglet.shiohara.persistence.repository.ecommerce;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.viglet.shiohara.persistence.model.ecommerce.ShEcomOrder;

public interface ShEcomOrderRepository extends JpaRepository<ShEcomOrder, String> {

	List<ShEcomOrder> findAll();
	
	Optional<ShEcomOrder> findById(String id);

	@SuppressWarnings("unchecked")
	ShEcomOrder save(ShEcomOrder shEcomOrder);

	@Modifying
	@Query("delete from ShEcomOrder eo where eo.id = ?1")
	void delete(String shEcomOrderId);
}
