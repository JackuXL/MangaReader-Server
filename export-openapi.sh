#!/bin/bash

# OpenAPI 文档导出脚本
# 用法: ./export-openapi.sh

set -e

# 配置
BASE_URL="${BASE_URL:-http://localhost:8080}"
OUTPUT_DIR="${OUTPUT_DIR:-./api-docs}"

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}OpenAPI 文档导出工具${NC}"
echo "================================"

# 创建输出目录
mkdir -p "$OUTPUT_DIR"
echo -e "输出目录: ${GREEN}$OUTPUT_DIR${NC}"

# 检查服务是否运行
echo -e "\n检查服务状态..."
if curl -s --fail "$BASE_URL/api-docs" > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} 服务正在运行 ($BASE_URL)"
else
    echo -e "${RED}✗${NC} 服务未运行"
    echo -e "${YELLOW}请先启动服务:${NC} mvn spring-boot:run"
    exit 1
fi

# 导出 JSON 格式
echo -e "\n导出 JSON 格式..."
if curl -s --fail "$BASE_URL/api-docs" -o "$OUTPUT_DIR/openapi.json"; then
    # 美化 JSON（如果安装了 jq）
    if command -v jq &> /dev/null; then
        jq '.' "$OUTPUT_DIR/openapi.json" > "$OUTPUT_DIR/openapi.formatted.json"
        mv "$OUTPUT_DIR/openapi.formatted.json" "$OUTPUT_DIR/openapi.json"
    fi
    SIZE=$(wc -c < "$OUTPUT_DIR/openapi.json" | tr -d ' ')
    echo -e "${GREEN}✓${NC} JSON 已导出 ($SIZE bytes)"
    echo -e "   文件: $OUTPUT_DIR/openapi.json"
else
    echo -e "${RED}✗${NC} JSON 导出失败"
fi

# 导出 YAML 格式
echo -e "\n导出 YAML 格式..."
if curl -s --fail "$BASE_URL/api-docs.yaml" -o "$OUTPUT_DIR/openapi.yaml"; then
    SIZE=$(wc -c < "$OUTPUT_DIR/openapi.yaml" | tr -d ' ')
    echo -e "${GREEN}✓${NC} YAML 已导出 ($SIZE bytes)"
    echo -e "   文件: $OUTPUT_DIR/openapi.yaml"
else
    echo -e "${RED}✗${NC} YAML 导出失败"
fi

# 显示 API 统计
if [ -f "$OUTPUT_DIR/openapi.json" ] && command -v jq &> /dev/null; then
    echo -e "\n${YELLOW}API 统计:${NC}"
    TITLE=$(jq -r '.info.title' "$OUTPUT_DIR/openapi.json")
    VERSION=$(jq -r '.info.version' "$OUTPUT_DIR/openapi.json")
    PATHS_COUNT=$(jq '.paths | length' "$OUTPUT_DIR/openapi.json")
    SCHEMAS_COUNT=$(jq '.components.schemas | length' "$OUTPUT_DIR/openapi.json")
    
    echo -e "  标题: $TITLE"
    echo -e "  版本: $VERSION"
    echo -e "  端点数: $PATHS_COUNT"
    echo -e "  模型数: $SCHEMAS_COUNT"
fi

echo -e "\n${GREEN}导出完成！${NC}"
echo -e "\n${YELLOW}下一步操作:${NC}"
echo "  1. 导入到 Postman/Insomnia"
echo "  2. 生成客户端代码:"
echo "     openapi-generator-cli generate -i $OUTPUT_DIR/openapi.json -g typescript-axios -o ./client"
echo "  3. 生成 HTML 文档:"
echo "     redoc-cli bundle $OUTPUT_DIR/openapi.json -o api-docs.html"
echo "  4. 在线查看:"
echo "     https://editor.swagger.io/ (粘贴内容)"

