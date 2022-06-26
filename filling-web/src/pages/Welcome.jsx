import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Card, Typography } from 'antd';

export default () => {
  return (
    <PageContainer>
      <Card>
        <Typography.Text strong>
          查看示例或
          {/* <FormattedMessage id="pages.welcome.advancedComponent" defaultMessage="Advanced Form" />{' '} */}
          <a
            href="https://zihjiang.github.io/filling-book/"
            rel="noopener noreferrer"
            target="__blank"
          >
            阅读文档
          </a>
        </Typography.Text>
      </Card>
    </PageContainer>
  );
};
