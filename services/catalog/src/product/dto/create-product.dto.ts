import { IsString, Min } from 'class-validator';
import { Type } from 'class-transformer';
export class CreateProductDto {
  @IsString()
  name: string;
@IsString()
description: string;
@Type(() => Number)
@Min(0, { message: 'El precio debe ser mayor o igual a 0' })
  priceCents: number;
  @Type(() => Number)
@Min(0, { message: 'El stock debe ser mayor o igual a 0' })
  stock: number;
  @Type(() => String)
  categoryId: string;
}
