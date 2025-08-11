package com.desitech.vyaparsathi.expense.mapper;

import com.desitech.vyaparsathi.expense.dto.ExpenseDto;
import com.desitech.vyaparsathi.expense.dto.UpdateExpenseDto;
import com.desitech.vyaparsathi.expense.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(target = "id", ignore = true)
    Expense toEntity(ExpenseDto dto);

    ExpenseDto toDto(Expense entity);

    List<ExpenseDto> toDtoList(List<Expense> entities);

    void updateEntityFromDto(UpdateExpenseDto dto, @MappingTarget Expense entity);
}