package com.desitech.vyaparsathi.expense.mapper;

import com.desitech.vyaparsathi.expense.dto.ExpenseDto;
import com.desitech.vyaparsathi.expense.dto.UpdateExpenseDto;
import com.desitech.vyaparsathi.expense.entity.Expense;
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
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public Expense toEntity(ExpenseDto dto) {
        if ( dto == null ) {
            return null;
        }

        Expense expense = new Expense();

        expense.setShopId( dto.getShopId() );
        expense.setType( dto.getType() );
        expense.setAmount( dto.getAmount() );
        expense.setDate( dto.getDate() );
        expense.setNotes( dto.getNotes() );

        return expense;
    }

    @Override
    public ExpenseDto toDto(Expense entity) {
        if ( entity == null ) {
            return null;
        }

        ExpenseDto expenseDto = new ExpenseDto();

        expenseDto.setId( entity.getId() );
        expenseDto.setShopId( entity.getShopId() );
        expenseDto.setType( entity.getType() );
        expenseDto.setAmount( entity.getAmount() );
        expenseDto.setDate( entity.getDate() );
        expenseDto.setNotes( entity.getNotes() );

        return expenseDto;
    }

    @Override
    public List<ExpenseDto> toDtoList(List<Expense> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ExpenseDto> list = new ArrayList<ExpenseDto>( entities.size() );
        for ( Expense expense : entities ) {
            list.add( toDto( expense ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(UpdateExpenseDto dto, Expense entity) {
        if ( dto == null ) {
            return;
        }

        entity.setType( dto.getType() );
        entity.setAmount( dto.getAmount() );
        entity.setDate( dto.getDate() );
        entity.setNotes( dto.getNotes() );
    }
}
