package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.dto.PurchaseOrderDto;
import com.desitech.vyaparsathi.inventory.dto.PurchaseOrderItemDto;
import com.desitech.vyaparsathi.inventory.entity.PurchaseOrder;
import com.desitech.vyaparsathi.inventory.entity.PurchaseOrderItem;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-19T13:17:59+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class PurchaseOrderMapperImpl implements PurchaseOrderMapper {

    @Override
    public PurchaseOrderDto toDto(PurchaseOrder purchaseOrder) {
        if ( purchaseOrder == null ) {
            return null;
        }

        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();

        Long id = purchaseOrderSupplierId( purchaseOrder );
        if ( id != null ) {
            purchaseOrderDto.setSupplierId( id.intValue() );
        }
        if ( purchaseOrder.getId() != null ) {
            purchaseOrderDto.setId( purchaseOrder.getId().intValue() );
        }
        purchaseOrderDto.setPoNumber( purchaseOrder.getPoNumber() );
        purchaseOrderDto.setOrderDate( purchaseOrder.getOrderDate() );
        purchaseOrderDto.setExpectedDeliveryDate( purchaseOrder.getExpectedDeliveryDate() );
        purchaseOrderDto.setTotalAmount( purchaseOrder.getTotalAmount() );
        purchaseOrderDto.setStatus( purchaseOrder.getStatus() );
        purchaseOrderDto.setItems( purchaseOrderItemListToPurchaseOrderItemDtoList( purchaseOrder.getItems() ) );

        return purchaseOrderDto;
    }

    @Override
    public PurchaseOrderItemDto toDto(PurchaseOrderItem purchaseOrderItem) {
        if ( purchaseOrderItem == null ) {
            return null;
        }

        PurchaseOrderItemDto purchaseOrderItemDto = new PurchaseOrderItemDto();

        purchaseOrderItemDto.setItemVariantId( purchaseOrderItemItemVariantId( purchaseOrderItem ) );
        purchaseOrderItemDto.setId( purchaseOrderItem.getId() );
        purchaseOrderItemDto.setQuantity( purchaseOrderItem.getQuantity() );
        purchaseOrderItemDto.setUnitCost( purchaseOrderItem.getUnitCost() );

        return purchaseOrderItemDto;
    }

    @Override
    public PurchaseOrder toEntity(PurchaseOrderDto dto) {
        if ( dto == null ) {
            return null;
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();

        if ( dto.getId() != null ) {
            purchaseOrder.setId( dto.getId().longValue() );
        }
        purchaseOrder.setPoNumber( dto.getPoNumber() );
        purchaseOrder.setOrderDate( dto.getOrderDate() );
        purchaseOrder.setExpectedDeliveryDate( dto.getExpectedDeliveryDate() );
        purchaseOrder.setTotalAmount( dto.getTotalAmount() );
        purchaseOrder.setStatus( dto.getStatus() );
        purchaseOrder.setItems( purchaseOrderItemDtoListToPurchaseOrderItemList( dto.getItems() ) );

        return purchaseOrder;
    }

    @Override
    public PurchaseOrderItem toEntity(PurchaseOrderItemDto dto) {
        if ( dto == null ) {
            return null;
        }

        PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();

        purchaseOrderItem.setId( dto.getId() );
        purchaseOrderItem.setQuantity( dto.getQuantity() );
        purchaseOrderItem.setUnitCost( dto.getUnitCost() );

        return purchaseOrderItem;
    }

    @Override
    public List<PurchaseOrderDto> toDtoList(List<PurchaseOrder> purchaseOrders) {
        if ( purchaseOrders == null ) {
            return null;
        }

        List<PurchaseOrderDto> list = new ArrayList<PurchaseOrderDto>( purchaseOrders.size() );
        for ( PurchaseOrder purchaseOrder : purchaseOrders ) {
            list.add( toDto( purchaseOrder ) );
        }

        return list;
    }

    private Long purchaseOrderSupplierId(PurchaseOrder purchaseOrder) {
        if ( purchaseOrder == null ) {
            return null;
        }
        Supplier supplier = purchaseOrder.getSupplier();
        if ( supplier == null ) {
            return null;
        }
        Long id = supplier.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<PurchaseOrderItemDto> purchaseOrderItemListToPurchaseOrderItemDtoList(List<PurchaseOrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<PurchaseOrderItemDto> list1 = new ArrayList<PurchaseOrderItemDto>( list.size() );
        for ( PurchaseOrderItem purchaseOrderItem : list ) {
            list1.add( toDto( purchaseOrderItem ) );
        }

        return list1;
    }

    private Long purchaseOrderItemItemVariantId(PurchaseOrderItem purchaseOrderItem) {
        if ( purchaseOrderItem == null ) {
            return null;
        }
        ItemVariant itemVariant = purchaseOrderItem.getItemVariant();
        if ( itemVariant == null ) {
            return null;
        }
        Long id = itemVariant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<PurchaseOrderItem> purchaseOrderItemDtoListToPurchaseOrderItemList(List<PurchaseOrderItemDto> list) {
        if ( list == null ) {
            return null;
        }

        List<PurchaseOrderItem> list1 = new ArrayList<PurchaseOrderItem>( list.size() );
        for ( PurchaseOrderItemDto purchaseOrderItemDto : list ) {
            list1.add( toEntity( purchaseOrderItemDto ) );
        }

        return list1;
    }
}
