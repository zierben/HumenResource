package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.SysDictType;
import com.hr.admin.entity.SysDictData;
import com.hr.admin.service.SysDictTypeService;
import com.hr.admin.service.SysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictTypeService sysDictTypeService;
    private final SysDictDataService sysDictDataService;

    @GetMapping("/types")
    public Result<List<SysDictType>> listTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDictType::getSortOrder);
        return Result.success(sysDictTypeService.list(wrapper));
    }

    @GetMapping("/types/{dictType}")
    public Result<SysDictType> getType(@PathVariable String dictType) {
        return Result.success(sysDictTypeService.lambdaQuery()
                .eq(SysDictType::getDictType, dictType)
                .one());
    }

    @PostMapping("/types")
    public Result<Void> saveType(@RequestBody SysDictType type) {
        sysDictTypeService.saveOrUpdate(type);
        return Result.success();
    }

    @PutMapping("/types/{id}")
    public Result<Void> updateType(@PathVariable Long id, @RequestBody SysDictType type) {
        type.setId(id);
        sysDictTypeService.updateById(type);
        return Result.success();
    }

    @DeleteMapping("/types/{id}")
    public Result<Void> deleteType(@PathVariable Long id) {
        SysDictType type = sysDictTypeService.getById(id);
        if (type != null) {
            sysDictDataService.lambdaUpdate()
                    .eq(SysDictData::getDictType, type.getDictType())
                    .remove();
        }
        sysDictTypeService.removeById(id);
        return Result.success();
    }

    @GetMapping("/data/{dictType}")
    public Result<List<SysDictData>> listData(@PathVariable String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.orderByAsc(SysDictData::getSortOrder);
        return Result.success(sysDictDataService.list(wrapper));
    }

    @PostMapping("/data")
    public Result<Void> saveData(@RequestBody SysDictData data) {
        sysDictDataService.saveOrUpdate(data);
        return Result.success();
    }

    @PutMapping("/data/{id}")
    public Result<Void> updateData(@PathVariable Long id, @RequestBody SysDictData data) {
        data.setId(id);
        sysDictDataService.updateById(data);
        return Result.success();
    }

    @DeleteMapping("/data/{id}")
    public Result<Void> deleteData(@PathVariable Long id) {
        sysDictDataService.removeById(id);
        return Result.success();
    }
}
