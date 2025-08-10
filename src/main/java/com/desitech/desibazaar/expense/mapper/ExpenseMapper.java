package com.desitech.desibazaar.expense.mapper;

import com.desitech.desibazaar.expense.dto.ExpenseDto;
import com.desitech.desibazaar.expense.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(target = "id", ignore = true)  // Ignore ID for create
    Expense toEntity(ExpenseDto dto);

    ExpenseDto toDto(Expense entity);
}