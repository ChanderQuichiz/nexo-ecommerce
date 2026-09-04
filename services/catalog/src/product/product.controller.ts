import {
  BadRequestException,
  Body,
  Controller,
  Post,
  UploadedFile,
  UseInterceptors,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express/multer/interceptors/file.interceptor';
import { CreateProductDto } from './dto/create-product.dto';
import type { Express } from 'express';
import { ProductService } from './product.service';
@Controller('product')
export class ProductController {

    constructor(private readonly ProductService: ProductService) {}

  @Post('create')
  @UseInterceptors(
    FileInterceptor('file', {
      limits: { fileSize: 5 * 1024 * 1024 },
      fileFilter: (req, file, callback) => {
        const validTypes = ['image/jpeg', 'image/png'];
        if (!validTypes.includes(file.mimetype)) {
          return callback(
            new BadRequestException('Tipo de archivo no permitido'),
            false,
          );
        }
        callback(null, true);
      },
    }),
  )
  uploadFile(
    @UploadedFile() file: Express.Multer.File,
    @Body product: CreateProductDto,
  ) {
    return this.filesService.upload(file);
  }
}
