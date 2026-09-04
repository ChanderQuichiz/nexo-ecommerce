import { Injectable } from '@nestjs/common';
import { CreateProductDto } from './dto/create-product.dto';

@Injectable()
export class ProductService {
    constructor(prismaService: PrismaService) {}
  create(product: CreateProductDto, file: Express.Multer.File) {
  }
}
