import {
  GetObjectCommand,
  DeleteObjectCommand,
  ListObjectsV2Command,
  PutObjectCommand,
  S3Client,
} from '@aws-sdk/client-s3';
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';
import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { getAwsClientConfig } from '../helpers/aws-config-helper';

@Injectable()
export class FileService {
  private readonly s3 = new S3Client(getAwsClientConfig());

  async upload(file: Express.Multer.File) {
    const key = `uploads/${Date.now()}-${file.originalname}`;

    await this.s3.send(
      new PutObjectCommand({
        Bucket: process.env.AWS_S3_BUCKET,
        Key: key,
        Body: file.buffer,
        ContentType: file.mimetype,
      }),
    );

    return { key };
  }

  async download(key: string) {
    try {
      const result = await this.s3.send(
        new GetObjectCommand({
          Bucket: process.env.AWS_S3_BUCKET,
          Key: key,
        }),
      );
      return result.Body;
    } catch (error: unknown) {
      if (error instanceof Error && error.name === 'NoSuchKey') {
        throw new NotFoundException('Archivo no encontrado');
      }
      throw new InternalServerErrorException('Error al acceder a S3');
    }
  }

  async delete(key: string) {
    await this.s3.send(
      new DeleteObjectCommand({
        Bucket: process.env.AWS_S3_BUCKET,
        Key: key,
      }),
    );
  }

  async list(prefix: string) {
    const result = await this.s3.send(
      new ListObjectsV2Command({
        Bucket: process.env.AWS_S3_BUCKET,
        Prefix: prefix,
      }),
    );
    return result.Contents;
  }

  async getDownloadUrl(key: string) {
    const command = new GetObjectCommand({
      Bucket: process.env.AWS_S3_BUCKET,
      Key: key,
    });
    return getSignedUrl(this.s3, command, { expiresIn: 3600 });
  }

  async getUploadUrl(key: string, contentType: string) {
    const command = new PutObjectCommand({
      Bucket: process.env.AWS_S3_BUCKET,
      Key: key,
      ContentType: contentType,
    });
    return getSignedUrl(this.s3, command, { expiresIn: 300 });
  }
}
